from typing import Optional

from websockets.server import serve as Serve

from socketd import SocketD
from socketd.transport.core.Costants import Constants
from socketd.transport.server.ServerBase import ServerBase
from socketd_websocket.WsAioChannelAssistant import WsAioChannelAssistant
from socketd.transport.server.ServerConfig import ServerConfig
from socketd_websocket.impl.AIOServe import AIOServe
from socketd_websocket.impl.AIOWebSocketServerImpl import AIOWebSocketServerImpl

from socketd.utils.LogConfig import logger, log


class WsAioServer(ServerBase):

    def __init__(self, config: ServerConfig):
        super().__init__(config, WsAioChannelAssistant(config))
        self._server: Optional[Serve] = None

    def __del__(self):
        del self._server

    def get_title(self):
        return "ws/aio/py-websocket/v" + SocketD.version();

    async def start(self):
        if self._isStarted:
            raise Exception("Socket.D server started")
        else:
            self._isStarted = True

        __host:str
        if self.get_config().get_host() is None:
            __host = "0.0.0.0"
        else:
            __host = self.get_config().get_host()

        self._server = AIOServe(ws_handler=None,
                           host=__host,
                           port=self.get_config().get_port(),
                           create_protocol=AIOWebSocketServerImpl,
                           ws_aio_server=self,
                           ssl=self.get_config().get_ssl_context(),
                           logger=logger,
                           max_size=Constants.MAX_SIZE_FRAME
                           )

        log.info("Socket.D server started: {server=" + self.get_config().get_local_url() + "}")
        await self._server
        return self

    async def stop(self):
        if self._isStarted:
            self._isStarted = False
        else:
            return

        await super().stop();

        if self._server is not None:
            self._server.ws_server.close()
            await self._server.ws_server.wait_closed()
