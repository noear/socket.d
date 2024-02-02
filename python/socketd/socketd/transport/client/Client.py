# 客户端
from abc import ABC, abstractmethod
from typing import Callable
from asyncio.futures import Future

from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.core import Listener
from socketd.transport.core.Processor import Processor
from socketd.transport.core.Session import Session
from socketd.transport.core.impl.HeartbeatHandlerDefault import HeartbeatHandler


class Client(ABC):

    @abstractmethod
    def heartbeatHandler(self, handler: Callable) -> 'Client': ...

    @abstractmethod
    def config(self, consumer: Callable[[ClientConfig], ClientConfig]) -> 'Client': ...

    @abstractmethod
    def listen(self, listener: Listener) -> 'Client': ...

    @abstractmethod
    def open(self) -> Session | Future: ...

    @abstractmethod
    def openOrThrow(self) -> Session | Future:...


class ClientInternal(Client):
    @abstractmethod
    def get_heartbeatHandler(self) -> HeartbeatHandler: ...

    @abstractmethod
    def get_heartbeatInterval(self) -> int: ...

    @abstractmethod
    def get_config(self) -> ClientConfig: ...

    @abstractmethod
    def get_processor(self) -> Processor: ...
