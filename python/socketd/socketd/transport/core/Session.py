import abc
from typing import Union, Dict, Any, Callable, Coroutine, Optional

from socket import gethostbyaddr

from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.core import HandshakeDefault
from socketd.transport.core.Message import Message
from socketd.transport.core.Entity import Entity


class Session(ClientSession, abc.ABC):

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
    async def send_ping(self) -> Callable | Coroutine:
        ...

    @abc.abstractmethod
    async def reply(self, from_msg: Message, content: Entity) -> None:
        ...

    @abc.abstractmethod
    async def reply_end(self, from_msg: Message, content: Entity) -> None:
        ...

    @abc.abstractmethod
    def generate_id(self) -> str:
        ...

    @abc.abstractmethod
    def set_session_id(self, value):
        ...

    @abc.abstractmethod
    def path(self) -> Optional[str]: ...

    @abc.abstractmethod
    def pathNew(self, path: str): ...
