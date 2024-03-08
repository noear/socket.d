# 客户端
from abc import ABC, abstractmethod
from asyncio.futures import Future

from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.client.ClientConfigHandler import ClientConfigHandler
from socketd.transport.client.ClientConnectHandler import ClientConnectHandler
from socketd.transport.core import Listener
from socketd.transport.core.Processor import Processor
from socketd.transport.core.Session import Session
from socketd.transport.client.ClientHeartbeatHandler import ClientHeartbeatHandler


class Client(ABC):
    @abstractmethod
    def connectHandler(self, connectHandler: ClientConnectHandler) -> 'Client': ...

    @abstractmethod
    def heartbeatHandler(self, heartbeatHandler: ClientHeartbeatHandler) -> 'Client': ...

    @abstractmethod
    def config(self, configHandler: ClientConfigHandler) -> 'Client': ...

    @abstractmethod
    def listen(self, listener: Listener) -> 'Client': ...

    @abstractmethod
    def open(self) -> Session | Future: ...

    @abstractmethod
    def openOrThrow(self) -> Session | Future:...


class ClientInternal(Client):
    @abstractmethod
    def get_connectHandler(self) -> ClientConnectHandler: ...

    @abstractmethod
    def get_heartbeatHandler(self) -> ClientHeartbeatHandler: ...

    @abstractmethod
    def get_heartbeatInterval(self) -> int: ...

    @abstractmethod
    def get_config(self) -> ClientConfig: ...

    @abstractmethod
    def get_processor(self) -> Processor: ...
