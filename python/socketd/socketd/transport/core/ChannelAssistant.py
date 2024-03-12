import typing
from typing import Any
from abc import ABC, abstractmethod
from socketd.transport.core.Frame import Frame

S = typing.TypeVar("S")


class ChannelAssistant(typing.Generic[S], ABC):
    @abstractmethod
    async def write(self, target: Any, frame: Frame) -> None:
        pass

    @abstractmethod
    def read(self, target: Any) -> Frame: ...

    @abstractmethod
    def is_valid(self, target: Any) -> bool:
        pass

    @abstractmethod
    async def close(self, target: Any) -> None:
        pass

    @abstractmethod
    def get_remote_address(self, target: Any) -> str:
        pass

    @abstractmethod
    def get_local_address(self, target: Any) -> str:
        pass
