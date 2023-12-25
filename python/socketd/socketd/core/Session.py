import abc
from typing import Union, Dict, Any, Callable, Awaitable, AsyncGenerator, Coroutine
from socket import gethostbyaddr
from socketd.core.Handshake import Handshake
from socketd.core.module.Message import Message
from socketd.core.module.Entity import Entity


class Session(abc.ABC):
    @abc.abstractmethod
    def is_valid(self) -> bool:
        ...

    @abc.abstractmethod
    def get_remote_address(self) -> gethostbyaddr:
        ...

    @abc.abstractmethod
    def get_local_address(self) -> gethostbyaddr:
        ...

    @abc.abstractmethod
    def get_handshake(self) -> Handshake:
        ...

    @abc.abstractmethod
    def get_attr_map(self) -> Dict[str, Any]:
        ...

    @abc.abstractmethod
    def get_attr(self, name: str) -> Union[None, Any]:
        ...

    @abc.abstractmethod
    def get_attr_or_default(self, name: str, default: Any) -> Any:
        ...

    @abc.abstractmethod
    def set_attr(self, name: str, value: Any) -> None:
        ...

    @abc.abstractmethod
    def get_session_id(self) -> str:
        ...

    @abc.abstractmethod
    def send_ping(self) -> Callable | Coroutine:
        ...

    @abc.abstractmethod
    async def send(self, event: str, content: Entity) -> None:
        ...

    @abc.abstractmethod
    async def send_and_request(self, event: str, content: Entity, timeout: int) -> Entity:
        ...

    @abc.abstractmethod
    async def send_stream_and_request(self, event: str, content: Entity,
                                      consumer: Callable[[Entity], Awaitable[Any]] | Coroutine[Entity, Any, None],
                                      timeout: int):
        """
        发送流和请求的抽象方法。

        Args:
            event (str): 事件名称。
            content (Entity): 内容实体。
            consumer (Callable[[Entity], Awaitable[Any]] | Coroutine[Entity, Awaitable[Any]]): 消费函数或协程，用来处理内容实体并返回异步结果。
            timeout (int): 超时时间。

        Returns:
            Awaitable[Any]: 消费函数或协程的异步结果。
        """
        ...

    @abc.abstractmethod
    async def send_and_subscribe(self, topic: str, content: Entity, consumer: Callable[[Entity], Any],
                                 timeout: int) -> None:
        ...

    @abc.abstractmethod
    def reply(self, from_msg: Message, content: Entity) -> None:
        ...

    @abc.abstractmethod
    def reply_end(self, from_msg: Message, content: Entity) -> None:
        ...

    @abc.abstractmethod
    def close(self):
        ...
