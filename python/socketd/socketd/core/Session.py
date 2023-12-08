import abc
from typing import Union, Dict, Any, Callable
from socket import gethostbyaddr
from socketd.core.Handshake import Handshake
from socketd.core.module.Message import Message
from socketd.core.module.Entity import Entity


class Session(abc.ABC):
    @abc.abstractmethod
    def is_valid(self) -> bool:
        pass

    @abc.abstractmethod
    def get_remote_address(self) -> gethostbyaddr:
        pass

    @abc.abstractmethod
    def get_local_address(self) -> gethostbyaddr:
        pass

    @abc.abstractmethod
    def get_handshake(self) -> Handshake:
        pass

    @abc.abstractmethod
    def get_attr_map(self) -> Dict[str, Any]:
        pass

    @abc.abstractmethod
    def get_attr(self, name: str) -> Union[None, Any]:
        pass

    @abc.abstractmethod
    def get_attr_or_default(self, name: str, default: Any) -> Any:
        pass

    @abc.abstractmethod
    def set_attr(self, name: str, value: Any) -> None:
        pass

    @abc.abstractmethod
    def get_session_id(self) -> str:
        pass

    @abc.abstractmethod
    def send_ping(self) -> None:
        pass

    @abc.abstractmethod
    async def send(self, topic: str, content: Entity) -> None:
        pass

    @abc.abstractmethod
    async def send_and_request(self, topic: str, content: Entity, timeout: int) -> Entity:
        pass

    @abc.abstractmethod
    def send_and_request_with_timeout(self, topic: str, content: Entity, timeout: int) -> Entity:
        pass

    @abc.abstractmethod
    def send_and_subscribe(self, topic: str, content: Entity, consumer: Callable[[Entity], Any]) -> None:
        pass

    @abc.abstractmethod
    def reply(self, from_msg: Message, content: Entity) -> None:
        pass

    @abc.abstractmethod
    def reply_end(self, from_msg: Message, content: Entity) -> None:
        pass

    def close(self):
        pass
