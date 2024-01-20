import abc
from typing import Union, Dict, Any, Callable, Coroutine, Optional
from asyncio import Future

from socket import gethostbyaddr
from socketd.transport.core import HandshakeDefault
from socketd.transport.core.Message import Message
from socketd.transport.core.Entity import Entity
from socketd.transport.stream.RequestStream import RequestStream
from socketd.transport.stream.SendStream import SendStream
from socketd.transport.stream import SubscribeStream


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
    def get_handshake(self) -> HandshakeDefault:
        ...

    @abc.abstractmethod
    def get_param(self, name: str): ...

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
    def send(self, event: str, content: Entity) -> SendStream:
        ...

    @abc.abstractmethod
    def send_and_request(self, event: str, content: Entity, timeout: int) -> RequestStream:
        ...

    @abc.abstractmethod
    def send_and_subscribe(self, event: str, content: Entity,
                                 timeout: int = 0) -> SubscribeStream:
        ...

    @abc.abstractmethod
    async def reply(self, from_msg: Message, content: Entity) -> None:
        ...

    @abc.abstractmethod
    async def reply_end(self, from_msg: Message, content: Entity) -> None:
        ...

    @abc.abstractmethod
    def close(self):
        ...

    @abc.abstractmethod
    def generate_id(self) -> str:
        ...

    @abc.abstractmethod
    def set_session_id(self, value):
        ...

    @abc.abstractmethod
    def reconnect(self) -> Future | None: ...

    @abc.abstractmethod
    def path(self) -> Optional[str]: ...

    @abc.abstractmethod
    def pathNew(self, path: str): ...
