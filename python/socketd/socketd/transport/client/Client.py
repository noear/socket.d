# 客户端
from abc import ABC, abstractmethod
from typing import Callable
from asyncio.futures import Future

from socketd.core.Processor import Processor
from socketd.core.Session import Session
from socketd.core.config.ClientConfig import ClientConfig
from socketd.transport.ChannelAssistant import ChannelAssistant


class Client(ABC):

    @abstractmethod
    def heartbeatHandler(self, handler: Callable) -> 'Client': ...

    @abstractmethod
    def config(self, consumer: Callable[[ClientConfig], None]) -> 'Client': ...

    @abstractmethod
    def process(self, processor: Callable) -> 'Client': ...

    @abstractmethod
    def listen(self, listener: Callable) -> 'Client': ...

    @abstractmethod
    def open(self) -> Session | Future: ...

    @abstractmethod
    def get_assistant(self) -> ChannelAssistant: ...

    @abstractmethod
    def get_heartbeatInterval(self): ...

    @abstractmethod
    def get_processor(self) -> Processor: ...

    @abstractmethod
    def get_heartbeatHandler(self): ...

    @abstractmethod
    def get_config(self): ...
