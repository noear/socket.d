import asyncio
from typing import Optional

from websockets.client import WebSocketClientProtocol

from socketd.exception.SocketDExecption import SocketDTimeoutException
from socketd.transport.client.Client import Client, ClientInternal
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Costants import Constants
from socketd.transport.core.config.logConfig import logger
from socketd.transport.client.ClientConnectorBase import ClientConnectorBase
from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd.transport.utils.async_api.AtomicRefer import AtomicRefer
from socketd_websocket.impl.AIOConnect import AIOConnect
from socketd_websocket.impl.AIOWebSocketClientImpl import AIOWebSocketClientImpl


class WsAioClientConnector(ClientConnectorBase):
    def __init__(self, client: ClientInternal):
        self._top: Optional[asyncio.Future] = None
        self.__real: Optional[AIOWebSocketClientImpl] = None
        self.__con: Optional[AIOConnect] = None
        self._loop: Optional[asyncio.AbstractEventLoop] = None
        super().__init__(client)

    async def connect(self) -> Channel:

        logger.info('Start connecting to: {}'.format(self.client.get_config().get_url()))

        # 处理自定义架构的影响
        ws_url = self.client.get_config().get_url().replace("std:", "").replace("-python", "")

        # 支持 ssl
        if self.client.get_config().get_ssl_context() is not None:
            ws_url = ws_url.replace("ws", "wss")
        self._loop = asyncio.new_event_loop()
        self._top = AsyncUtil.run_forever(self._loop)
        try:
            self.__con: AIOConnect = AIOConnect(ws_url, client=self.client,
                                                ssl=self.client.get_config().get_ssl_context(),
                                                create_protocol=AIOWebSocketClientImpl,
                                                ping_timeout=self.client.get_config().get_idle_timeout(),
                                                ping_interval=self.client.get_config().get_idle_timeout(),
                                                logger=logger,
                                                max_size=Constants.MAX_SIZE_FRAME,
                                                message_loop=self._loop
                                                )
            self.__real: AIOWebSocketClientImpl | WebSocketClientProtocol = await self.__con
            handshakeResult: ClientHandshakeResult = await self.__real.handshake_future.get(
                self.client.get_config().get_connect_timeout())
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
            raise SocketDTimeoutException(f"Connection timeout: {self.client.get_config().get_link_url()} {e}")

    async def close(self):
        if self.__real is None:
            return
        try:
            await self.__real.close()
            self.__real.on_close()
            await self.stop()
        except Exception as e:
            logger.debug(e)

    async def stop(self):
        if self._top:
            _top = self._top
            if not _top.done():
                _top.set_result(1)
        self._loop.stop()
        logger.debug(f"Stopping WebSocket::{self._loop.is_running()}")
