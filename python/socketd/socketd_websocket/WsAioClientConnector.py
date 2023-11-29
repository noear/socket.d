import asyncio
import logging
import nest_asyncio

from websockets.client import connect as Connect

from socketd.core.Channel import Channel
from socketd.transport.client.ClientConnectorBase import ClientConnectorBase
from socketd_websocket.impl.AIOConnect import AIOConnect
from socketd_websocket.impl.AIOWebSocketClientImpl import AIOWebSocketClientImpl

logger = logging.getLogger(__name__)


class WsAioClientConnector(ClientConnectorBase):
    def __init__(self, client: 'WsAioClient'):
        self.client: 'WsAioClient' = client
        self.real: Connect = None
        self.__loop = asyncio.get_event_loop()
        self.__stop = asyncio.Future()
        super().__init__(client)
        nest_asyncio.apply()

    def connect(self) -> Channel:
        logger.debug('Start connecting to: {}'.format(self.client.get_config().url))

        # 处理自定义架构的影响
        ws_url = self.client.get_config().url.replace('-python://', '://').replace("std:", "")

        # 支持 ssl
        if self.client.get_config().get_ssl_context() is not None:
            ws_url = ws_url.replace("ws", "wss")
        try:
            con = AIOConnect(ws_url, client=self.client, ssl=self.client.get_config().get_ssl_context(),
                             create_protocol=AIOWebSocketClientImpl)
            self.real: AIOConnect = self.__loop.run_until_complete(con)
            return self.real.get_channel()
        except RuntimeError as e:
            raise e
        except Exception as e:
            raise e

    async def close(self):
        if self.real is None:
            return
        try:
            await self.__stop
        except Exception as e:
            logger.debug('{}'.format(e))
