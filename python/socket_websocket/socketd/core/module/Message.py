from abc import ABC, abstractmethod

from .Entity import Entity


class Message(ABC):
    @abstractmethod
    def is_request(self) -> bool:
        pass

    @abstractmethod
    def is_subscribe(self) -> bool:
        pass

    @abstractmethod
    def is_close(self) -> bool:
        pass

    @abstractmethod
    def get_sid(self) -> str:
        pass

    @abstractmethod
    def get_topic(self) -> str:
        pass

    @abstractmethod
    def get_entity(self) -> Entity:
        pass
