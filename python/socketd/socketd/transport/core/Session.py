import abc
from typing import Union, Dict, Any, Callable, Coroutine, Optional

from socket import gethostbyaddr

from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.core import HandshakeDefault
from socketd.transport.core.Message import Message
from socketd.transport.core.Entity import Entity


class Session(ClientSession):

    @abc.abstractmethod
    def remote_address(self) -> gethostbyaddr:
        ...

    @abc.abstractmethod
    def local_address(self) -> gethostbyaddr:
        ...

    @abc.abstractmethod
    def handshake(self) -> HandshakeDefault:
        ...

    def name(self):
        self.param("@")

    @abc.abstractmethod
    def param(self, name: str) -> str:
        ...

    @abc.abstractmethod
    def param_or_default(self, name: str, defVal: str) -> str:
        ...

    @abc.abstractmethod
    def path(self) -> Optional[str]: ...

    @abc.abstractmethod
    def path_new(self, pathNew: str): ...

    @abc.abstractmethod
    def attr_map(self) -> Dict[str, Any]:
        ...

    @abc.abstractmethod
    def attr_has(self, name: str) -> bool:
        ...

    @abc.abstractmethod
    def attr(self, name: str) -> Union[None, Any]:
        ...

    @abc.abstractmethod
    def attr_or_default(self, name: str, defVal: Any) -> Any:
        ...

    @abc.abstractmethod
    def attr_put(self, name: str, value: Any) -> None:
        ...

    @abc.abstractmethod
    async def send_ping(self) -> Callable | Coroutine:
        ...

    @abc.abstractmethod
    async def send_alarm(self, _from: Message, alarm: str) -> None:
        ...

    @abc.abstractmethod
    async def reply(self, from_msg: Message, content: Entity) -> None:
        ...

    @abc.abstractmethod
    async def reply_end(self, from_msg: Message, content: Entity) -> None:
        ...

    @abc.abstractmethod
    def live_time(self):
        ...

    @abc.abstractmethod
    def generate_id(self) -> str:
        ...
