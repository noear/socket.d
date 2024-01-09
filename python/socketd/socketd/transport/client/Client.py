# 客户端
from abc import ABC, abstractmethod
from typing import Callable
from asyncio.futures import Future

from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.core import Listener
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.Processor import Processor
from socketd.transport.core.Session import Session


class Client(ABC):

    @abstractmethod
    def heartbeatHandler(self, handler: Callable) -> 'Client': ...

    @abstractmethod
    def config(self, consumer: Callable[[ClientConfig], ClientConfig]) -> 'Client': ...

    @abstractmethod
    def process(self, processor: Callable) -> 'Client': ...

    @abstractmethod
    def listen(self, listener: Listener) -> 'Client': ...

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
    def get_config(self) -> ClientConfig: ...
