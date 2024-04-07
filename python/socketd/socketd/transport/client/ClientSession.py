import abc

from socketd.transport.core import Entity
from socketd.transport.stream.RequestStream import RequestStream
from socketd.transport.stream.SendStream import SendStream
from socketd.transport.stream.SubscribeStream import SubscribeStream


class ClientSession:

    @abc.abstractmethod
    def is_valid(self) -> bool:
        ...

    @abc.abstractmethod
    def is_closing(self)->bool:
        ...

    @abc.abstractmethod
    def session_id(self) -> str:
        ...

    @abc.abstractmethod
    async def send(self, event: str, content: Entity) -> SendStream:
        ...

    @abc.abstractmethod
    async def send_and_request(self, event: str, content: Entity, timeout: int = 0) -> RequestStream:
        ...

    @abc.abstractmethod
    async def send_and_subscribe(self, event: str, content: Entity, timeout: int = 0) -> SubscribeStream:
        ...

    async def close_starting(self):
        self.preclose()

    @abc.abstractmethod
    async def preclose(self):
        ...

    @abc.abstractmethod
    async def close(self):
        ...

    @abc.abstractmethod
    async def reconnect(self):
        ...
