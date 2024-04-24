from __future__ import annotations

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


# 客户端（用于构建会话）
class Client(ABC):
    # 连接处理
    @abstractmethod
    def connect_handler(self, connectHandler: ClientConnectHandler) -> Client: ...

    # 心跳处理
    @abstractmethod
    def heartbeat_handler(self, heartbeatHandler: ClientHeartbeatHandler) -> Client: ...

    # 配置处理
    @abstractmethod
    def config(self, configHandler: ClientConfigHandler) -> Client: ...

    # 监听
    @abstractmethod
    def listen(self, listener: Listener) -> Client: ...

    # 打开会话
    @abstractmethod
    async def open(self) -> Session: ...

    # 打开会话或出异常（即要求第一次是连接成功的）
    @abstractmethod
    async def open_or_throw(self) -> Session: ...


class ClientInternal(Client):
    @abstractmethod
    def get_connect_handler(self) -> ClientConnectHandler: ...

    @abstractmethod
    def get_heartbeat_handler(self) -> ClientHeartbeatHandler: ...

    @abstractmethod
    def get_heartbeat_interval(self) -> int: ...

    @abstractmethod
    def get_config(self) -> ClientConfig: ...

    @abstractmethod
    def get_processor(self) -> Processor: ...
