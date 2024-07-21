import asyncio
from typing import Optional

from websockets import Subprotocol
from websockets.client import WebSocketClientProtocol

from socketd import SocketD
from socketd.exception.SocketDExecption import SocketDTimeoutException
from socketd.transport.client.Client import ClientInternal
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Costants import Constants
from socketd.utils.LogConfig import log, logger
from socketd.transport.client.ClientConnectorBase import ClientConnectorBase
from socketd.utils.AsyncUtils import AsyncUtils
from socketd_websocket.impl.AIOConnect import AIOConnect
from socketd_websocket.impl.AIOWebSocketClientImpl import AIOWebSocketClientImpl


class WsAioClientConnector(ClientConnectorBase):
    def __init__(self, client: ClientInternal):
        self._top: Optional[asyncio.Future] = None
        self._loop: Optional[asyncio.AbstractEventLoop] = None
        self.__real: Optional[AIOWebSocketClientImpl] = None
        self.__con: Optional[AIOConnect] = None
        super().__init__(client)

    async def connect(self) -> Channel:
        # 关闭之前的资源
        await self.close()

        # 处理自定义架构的影响
        ws_url = self.client.get_config().get_url().replace("-python://", "://")

        # 支持 ssl
        if self.client.get_config().get_ssl_context() is not None:
            ws_url = ws_url.replace("ws", "wss")

        self._loop = asyncio.new_event_loop()
        self._top = AsyncUtils.run_forever(self._loop, daemon=True)

        try:
            if self.client.get_config().is_use_subprotocols():
                self.__con: AIOConnect = AIOConnect(uri=ws_url,
                                                    client=self.client,
                                                    ssl=self.client.get_config().get_ssl_context(),
                                                    create_protocol=AIOWebSocketClientImpl,
                                                    subprotocols=[Subprotocol(SocketD.protocol_name())],
                                                    ping_timeout=self.client.get_config().get_idle_timeout() / 1000,
                                                    ping_interval=self.client.get_config().get_idle_timeout() / 1000,
                                                    logger=logger,
                                                    max_size=Constants.MAX_SIZE_FRAME,
                                                    message_loop=self._loop)
            else:
                self.__con: AIOConnect = AIOConnect(uri=ws_url,
                                                    client=self.client,
                                                    ssl=self.client.get_config().get_ssl_context(),
                                                    create_protocol=AIOWebSocketClientImpl,
                                                    ping_timeout=self.client.get_config().get_idle_timeout() / 1000,
                                                    ping_interval=self.client.get_config().get_idle_timeout() / 1000,
                                                    logger=logger,
                                                    max_size=Constants.MAX_SIZE_FRAME,
                                                    message_loop=self._loop)

            self.__real: AIOWebSocketClientImpl | WebSocketClientProtocol = await self.__con
            handshakeResult: ClientHandshakeResult = await self.__real.handshake_future.get(self.client.get_config().get_connect_timeout() / 1000)

            if _e := handshakeResult.get_throwable():
                raise _e
            else:
                return handshakeResult.get_channel()
        except TimeoutError as t:
            await self.close()
            raise SocketDTimeoutException(f"Connection timeout: {self.client.get_config().get_link_url()}")
        except IOError as o:
            await self.close()
            raise o
        except Exception as e:
            await self.close()
            raise SocketDTimeoutException(f"Connection failed: {self.client.get_config().get_link_url()} {e}")

    async def close(self):
        try:
            if self.__real:
                await self.__real.close()
                await self.__real.on_close()

            if self._top:
                if not self._top.done():
                    self._top.set_result(1)

            if self._loop:
                self._loop.stop()

        except Exception as e:
            log.debug("Client connector close error", e)
