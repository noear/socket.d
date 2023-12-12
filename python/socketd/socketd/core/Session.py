import abc
from typing import Union, Dict, Any, Callable, Awaitable
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
    def send_ping(self) -> None:
        ...

    @abc.abstractmethod
    async def send(self, topic: str, content: Entity) -> None:
        ...

    @abc.abstractmethod
    async def send_and_request(self, topic: str, content: Entity, timeout: int) -> Entity:
        ...

    async def send_stream_and_request(self, event: str, content: Entity, consumer: Callable[[Entity], Awaitable[Any]],
                                      timeout: int): ...

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

    def close(self):
        ...
