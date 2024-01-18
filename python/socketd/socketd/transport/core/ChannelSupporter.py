import typing
from abc import ABC, abstractmethod
from typing import TypeVar

from socketd.transport.core import Config
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.Processor import Processor

S = TypeVar("S")


class ChannelSupporter(typing.Generic[S], ABC):

    @abstractmethod
    def get_processor(self) -> Processor: ...

    @abstractmethod
    def get_config(self) -> Config: ...

    @abstractmethod
    def get_assistant(self) -> ChannelAssistant[S]: ...
