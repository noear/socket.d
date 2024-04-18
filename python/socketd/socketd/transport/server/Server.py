from __future__ import annotations

from typing import Callable, Coroutine

from websockets.sync.server import WebSocketServer

from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.Listener import Listener


class Server:
    def get_title(self): ...

    def get_config(self) -> ServerConfig: ...

    def config(self, consumer: Callable[[ServerConfig], None]) -> Server: ...

    def listen(self, listener: Listener) -> Server: ...

    def start(self) -> WebSocketServer | Coroutine: ...

    def prestop(self) -> Coroutine: ...

    def stop(self) -> Coroutine: ...
