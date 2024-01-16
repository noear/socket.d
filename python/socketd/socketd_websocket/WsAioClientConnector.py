import asyncio
from typing import Optional

from websockets.client import WebSocketClientProtocol

from socketd.transport.client.Client import Client, ClientInternal
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.transport.core.Channel import Channel
from socketd.transport.core.config.logConfig import logger
from socketd.transport.client.ClientConnectorBase import ClientConnectorBase
from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd.transport.utils.async_api.AtomicRefer import AtomicRefer
from socketd_websocket.impl.AIOConnect import AIOConnect
from socketd_websocket.impl.AIOWebSocketClientImpl import AIOWebSocketClientImpl


class WsAioClientConnector(ClientConnectorBase):
    def __init__(self, client: ClientInternal):
        self.__top: Optional[AtomicRefer[asyncio.Future]] = None
        self.__real: Optional[AIOWebSocketClientImpl] = None
        self.__con: Optional[AIOConnect] = None
        self.__loop = asyncio.new_event_loop()
        super().__init__(client)

    async def connect(self) -> Channel:
        logger.info('Start connecting to: {}'.format(self.client.get_config().get_url()))

        # 处理自定义架构的影响
        ws_url = self.client.get_config().get_url().replace("std:", "").replace("-python", "")

        # 支持 ssl
        if self.client.get_config().get_ssl_context() is not None:
            ws_url = ws_url.replace("ws", "wss")
        if self.__top is None:
            self.__top = AtomicRefer(AsyncUtil.run_forever(self.__loop))
        try:
            self.__con: AIOConnect = AIOConnect(ws_url, client=self.client,
                                                ssl=self.client.get_config().get_ssl_context(),
                                                create_protocol=AIOWebSocketClientImpl,
                                                ping_timeout=self.client.get_config().get_idle_timeout(),
                                                ping_interval=self.client.get_config().get_idle_timeout(),
                                                logger=logger,
                                                max_size=self.client.get_config().get_ws_max_size(),
                                                message_loop=self.__loop
                                                )
            self.__real: AIOWebSocketClientImpl | WebSocketClientProtocol = await self.__con
            handshakeResult: ClientHandshakeResult = await self.__real.handshake_future.get(self.client.get_config().get_connect_timeout())
            if _e := handshakeResult.get_throwable():
                raise _e
            else:
                return handshakeResult.get_channel()
        except Exception as e:
            raise e

    async def close(self):
        if self.__real is None:
            return
        try:
            async with self.__top:
                __top = await self.__top.get()
                if not __top.done():
                    __top.set_result(1)
            self.__real.on_close()
            self.__loop.stop()
        except Exception as e:
            logger.debug(e)
