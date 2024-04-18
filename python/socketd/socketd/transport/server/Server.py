from __future__ import annotations

from typing import Callable, Coroutine

from websockets.sync.server import WebSocketServer

from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.Listener import Listener

#服务端接口
class Server:
    #获取台头
    def get_title(self): ...

    #获取配置
    def get_config(self) -> ServerConfig: ...

    #配置
    def config(self, consumer: Callable[[ServerConfig], None]) -> Server: ...

    #监听
    def listen(self, listener: Listener) -> Server: ...

    #启动
    def start(self) -> WebSocketServer | Coroutine: ...

    #预停止
    def prestop(self) -> Coroutine: ...

    #停止
    def stop(self) -> Coroutine: ...
