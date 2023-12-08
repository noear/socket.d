from typing import Any
from abc import ABC, abstractmethod


class Channel(ABC):
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
    def get_config(self) -> 'Config':
        pass

    @abstractmethod
    def get_requests(self) -> int:
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
    def send_connect(self, url: str) -> None:
        pass

    @abstractmethod
    def send_connack(self, connect_message: 'Message') -> None:
        pass

    @abstractmethod
    def send_ping(self) -> None:
        pass

    @abstractmethod
    def send_pong(self) -> None:
        pass

    @abstractmethod
    def send_close(self) -> None:
        pass

    @abstractmethod
    def send(self, frame: 'Frame', acceptor: 'Acceptor') -> None:
        pass

    @abstractmethod
    def retrieve(self, frame: 'Frame') -> None:
        pass

    @abstractmethod
    def get_session(self) -> 'Session':
        pass