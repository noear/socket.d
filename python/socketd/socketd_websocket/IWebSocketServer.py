import abc

from websockets.server import WebSocketServerProtocol


class IWebSocketServer:

    @abc.abstractmethod
    def handshake_handler(self): ...

    @abc.abstractmethod
    def on_open(self, conn: WebSocketServerProtocol): ...

    @abc.abstractmethod
    async def on_error(self, conn: WebSocketServerProtocol, e: Exception): ...

    @abc.abstractmethod
    async def on_message(self, conn: WebSocketServerProtocol, message: bytes): ...

    @abc.abstractmethod
    async def on_close(self, conn: WebSocketServerProtocol): ...

