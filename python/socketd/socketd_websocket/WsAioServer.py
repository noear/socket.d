from typing import Optional

from websockets.server import WebSocketServer, serve as Serve, WebSocketServerProtocol
from websockets import broadcast

from socketd.transport.core.Costants import Constants
from socketd.transport.server.ServerBase import ServerBase
from socketd_websocket.WsAioChannelAssistant import WsAioChannelAssistant
from socketd.transport.server.ServerConfig import ServerConfig
from socketd_websocket.impl.AIOServe import AIOServe
from socketd_websocket.impl.AIOWebSocketServerImpl import AIOWebSocketServerImpl

from socketd.transport.core.impl.LogConfig import logger, log


class WsAioServer(ServerBase):

    def __init__(self, config: ServerConfig):
        super().__init__(config, WsAioChannelAssistant(config))
        self._server: Optional[Serve] = None

    async def start(self) -> WebSocketServer:
        if self._isStarted:
            raise Exception("Server started")
        else:
            self._isStarted = True
        _server = AIOServe(ws_handler=None,
                           host="0.0.0.0" if self.get_config().get_host() is None else self.get_config().get_host(),
                           port=self.get_config().get_port(),
                           create_protocol=AIOWebSocketServerImpl,
                           ws_aio_server=self,
                           ping_interval=self.get_config().get_idle_timeout(),
                           ping_timeout=self.get_config().get_idle_timeout(),
                           ssl=self.get_config().get_ssl_context(),
                           logger=logger,
                           max_size=Constants.MAX_SIZE_FRAME,
                           )
        self._server = _server
        log.info("Server started: {server=" + self.get_config().get_local_url() + "}")
        return await self._server

    def message_all(self, message: str):
        """广播"""
        broadcast(self._server.ws_server.websockets, message)

    def register(self, protocol: WebSocketServerProtocol) -> None:
        """注册"""
        self._server.ws_server.register(protocol)

    async def stop(self):
        if self._isStarted:
            self._isStarted = False
        else:
            return

        await super().stop();

        if self._server is not None:
            self._server.ws_server.close()
            await self._server.ws_server.wait_closed()
