from __future__ import annotations

from typing import Callable, Coroutine

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
    async def start(self) -> Server: ...

    #预停止
    async def prestop(self): ...

    #停止
    async def stop(self): ...
