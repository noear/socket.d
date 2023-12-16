from typing import Any

from socketd.core.async_api.AtomicRefer import AtomicRefer
from .Costants import Function
from .Session import Session
from .config.Config import Config
from .module.Frame import Frame
from .module.Message import Message
from abc import abstractmethod


class Channel:
    @abstractmethod
    def get_attachment(self, name: str) -> Any:
        ...

    @abstractmethod
    def set_attachment(self, name: str, val: Any) -> None:
        ...

    @abstractmethod
    def remove_acceptor(self, sid: str) -> None:
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
    def set_handshake(self, handshake: 'Handshake') -> None:
        ...

    @abstractmethod
    def get_handshake(self) -> 'Handshake':
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
    async def send(self, frame: 'Frame', acceptor: 'Acceptor') -> None:
        ...

    @abstractmethod
    async def retrieve(self, frame: Frame, on_error: Function) -> None:
        ...

    @abstractmethod
    def get_session(self) -> Session:
        ...

    async def close(self, code: int = 1000,
                    reason: str = "", ):
        ...

    def on_error(self, error: Exception):
        ...
