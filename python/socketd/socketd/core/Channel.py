from typing import Any

from socketd.core.async_api.AtomicRefer import AtomicRefer
from .Session import Session
from .config.Config import Config
from .module.Frame import Frame
from .module.Message import Message
from abc import abstractmethod


class Channel:
    @abstractmethod
    def get_attachment(self, name: str) -> Any:
        pass

    @abstractmethod
    def set_attachment(self, name: str, val: Any) -> None:
        pass

    @abstractmethod
    def remove_acceptor(self, sid: str) -> None:
        pass

    @abstractmethod
    def is_valid(self) -> bool:
        pass

    @abstractmethod
    def is_closed(self) -> bool:
        pass

    @abstractmethod
    def get_config(self) -> Config:
        pass

    @abstractmethod
    def get_requests(self) -> AtomicRefer:
        pass

    @abstractmethod
    def set_handshake(self, handshake: 'Handshake') -> None:
        pass

    @abstractmethod
    def get_handshake(self) -> 'Handshake':
        pass

    @abstractmethod
    def get_remote_address(self) -> str:
        pass

    @abstractmethod
    def get_local_address(self) -> str:
        pass

    @abstractmethod
    def set_live_time(self) -> None:
        pass

    @abstractmethod
    def get_live_time(self) -> int:
        pass

    @abstractmethod
    async def send_connect(self, url: str) -> None:
        pass

    @abstractmethod
    async def send_connack(self, connect_message: Message) -> None:
        pass

    @abstractmethod
    async def send_ping(self) -> None:
        pass

    @abstractmethod
    async def send_pong(self) -> None:
        pass

    @abstractmethod
    async def send_close(self) -> None:
        pass

    @abstractmethod
    async def send(self, frame: 'Frame', acceptor: 'Acceptor') -> None:
        pass

    @abstractmethod
    def retrieve(self, frame: Frame) -> None:
        pass

    @abstractmethod
    def get_session(self) -> Session:
        pass

    async def close(self, code: int = 1000,
                    reason: str = "", ):
        pass
