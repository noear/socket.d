from abc import ABC, abstractmethod

from .Entity import Entity, Reply
from .EntityMetas import EntityMetas


class Message(Reply, ABC):
    def at_name(self) -> str:
        return self.meta("@")

    def range_start(self):
        return self.meta_as_int(EntityMetas.META_RANGE_START)

    def range_size(self):
        return self.meta_as_int(EntityMetas.META_RANGE_SIZE)

    @abstractmethod
    def is_request(self) -> bool:
        ...

    @abstractmethod
    def is_subscribe(self) -> bool:
        ...

    @abstractmethod
    def event(self) -> str:
        ...

    @abstractmethod
    def entity(self) -> Entity:
        ...


class MessageInternal(Message, ABC):
    @abstractmethod
    def flag(self) -> int:
        ...
