import asyncio
from typing import Any, Optional
from asyncio import Future

from socketd.transport.core.HandshakeDefault import HandshakeDefault
from socketd.transport.core.Session import Session
from socketd.transport.core.Config import Config
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Message import Message
from abc import abstractmethod, ABC

from socketd.transport.stream.StreamManger import StreamInternal


class Channel(ABC):
    @abstractmethod
    def get_attachment(self, name: str) -> Any:
        ...

    @abstractmethod
    def put_attachment(self, name: str, val: Any) -> None:
        ...

    @abstractmethod
    def is_valid(self) -> bool:
        ...

    @abstractmethod
    def is_closing(self) -> bool:
        ...

    @abstractmethod
    def is_closed(self) -> int:
        ...

    @abstractmethod
    def get_config(self) -> Config:
        ...


    @abstractmethod
    def set_handshake(self, handshake: 'HandshakeDefault') -> None:
        ...

    @abstractmethod
    def get_handshake(self) -> 'HandshakeDefault':
        ...

    @abstractmethod
    def get_remote_address(self) -> str:
        ...

    @abstractmethod
    def get_local_address(self) -> str:
        ...

    @abstractmethod
    def get_live_time(self) -> int:
        ...

    @abstractmethod
    async def send_connect(self, url: str,  metaMap: dict) -> None:
        ...

    @abstractmethod
    async def send_connack(self, connect_message: Message) -> None:
        ...

    @abstractmethod
    async def send_ping(self) -> None:
        ...

    @abstractmethod
    async def send_pong(self) -> None:
        ...

    @abstractmethod
    async def send_close(self, code:int) -> None:
        ...

    @abstractmethod
    async def send_alarm(self, _from: Message, alarm:str) -> None:
        ...

    @abstractmethod
    async def send(self, frame: 'Frame', stream: Optional[StreamInternal]) -> None:
        ...

    @abstractmethod
    def retrieve(self, frame: Frame, stream: StreamInternal) -> None:
        ...

    @abstractmethod
    def get_session(self) -> Session:
        ...

    @abstractmethod
    async def close(self, code):
        ...

    @abstractmethod
    def on_error(self, error: Exception):
        ...

    @abstractmethod
    def reconnect(self) -> Future | None: ...

    @abstractmethod
    def get_loop(self) -> asyncio.AbstractEventLoop: ...

    @abstractmethod
    def set_loop(self, loop) -> None: ...
