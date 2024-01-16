import asyncio
from typing import Any
from asyncio import Future

from socketd.transport.core.Session import Session
from socketd.transport.core.Config import Config
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Message import Message
from abc import abstractmethod

from socketd.transport.utils.sync_api.AtomicRefer import AtomicRefer


class Channel:
    @abstractmethod
    def get_attachment(self, name: str) -> Any:
        ...

    @abstractmethod
    def set_attachment(self, name: str, val: Any) -> None:
        ...

    @abstractmethod
    def is_valid(self) -> bool:
        ...

    @abstractmethod
    def is_closed(self) -> bool:
        ...

    @abstractmethod
    def get_config(self) -> Config:
        ...

    @abstractmethod
    def get_requests(self) -> AtomicRefer:
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
    def set_live_time(self) -> None:
        ...

    @abstractmethod
    def get_live_time(self) -> int:
        ...

    @abstractmethod
    async def send_connect(self, url: str) -> None:
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
    async def send_close(self) -> None:
        ...

    @abstractmethod
    async def send(self, frame: 'Frame', stream: 'StreamInternal') -> None:
        ...

    @abstractmethod
    async def retrieve(self, frame: Frame, stream: 'StreamInternal') -> None:
        ...

    @abstractmethod
    def get_session(self) -> Session:
        ...

    async def close(self, code: int = 1000,
                    reason: str = "", ):
        ...

    def on_error(self, error: Exception):
        ...

    def reconnect(self) -> Future | None: ...

    def get_loop(self) -> asyncio.AbstractEventLoop: ...

    def set_loop(self, loop) -> None: ...
