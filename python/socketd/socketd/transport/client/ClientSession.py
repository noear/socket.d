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
    def get_session_id(self) -> str:
        ...

    @abc.abstractmethod
    async def send(self, event: str, content: Entity) -> SendStream:
        ...

    @abc.abstractmethod
    async def send_and_request(self, event: str, content: Entity, timeout: int) -> RequestStream:
        ...

    @abc.abstractmethod
    async def send_and_subscribe(self, event: str, content: Entity,
                                 timeout: int = 0) -> SubscribeStream:
        ...

    @abc.abstractmethod
    def close(self):
        ...

    @abc.abstractmethod
    def reconnect(self) -> None: ...
