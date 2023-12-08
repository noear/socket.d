import asyncio
from loguru import logger

from websockets.server import WebSocketServer, serve as Serve, WebSocketServerProtocol
from websockets import broadcast

from socketd.transport.server.ServerBase import ServerBase
from .WsAioChannelAssistant import WsAioChannelAssistant
from socketd.core.config.ServerConfig import ServerConfig
from .impl.AIOServe import AIOServe
from socketd_websocket.impl.AIOWebSocketServerImpl import AIOWebSocketServerImpl


class WsAioServer(ServerBase):

    def __init__(self, config: ServerConfig):
        super().__init__(config, WsAioChannelAssistant(config))
        self.__loop = asyncio.get_event_loop()
        self.server: Serve = None
        self._stop = asyncio.Future()  # set this future to exit the server

    async def start(self) -> 'WebSocketServer':
        if self.isStarted:
            raise Exception("Server started")
        else:
            self.isStarted = True
        if self._config.get_host() is not None:
            _server = AIOServe(ws_handler=None,
                               host="0.0.0.0", port=self._config.get_port(),
                               create_protocol=AIOWebSocketServerImpl,
                               ws_aio_server=self,
                               ssl=self._config.get_ssl_context())
        else:
            _server = AIOServe(ws_handler=None,
                               host=self._config.get_host(), port=self._config.get_port(),
                               create_protocol=AIOWebSocketServerImpl,
                               ws_aio_server=self,
                               ssl=self._config.get_ssl_context())
        self.server = _server
        logger.info("Server started: {server=" + self._config.get_local_url() + "}")
        return await self.server

    def message_all(self, message: str):
        """广播"""
        broadcast(self.server.ws_server.websockets, message)

    def register(self, protocol: WebSocketServerProtocol) -> None:
        """注册"""
        self.server.ws_server.register(protocol)

    async def stop(self):
        logger.info("WsAioServer stop...")
        await self.server.ws_server.close()
        await self._stop
