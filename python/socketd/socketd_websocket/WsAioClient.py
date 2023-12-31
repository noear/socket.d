import asyncio

from loguru import logger

from socketd.transport.core.Channel import Channel
from socketd.transport.core.Session import Session
from socketd.transport.core.internal.SessionDefault import SessionDefault
from socketd.transport.client.ClientBase import ClientBase
from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.client.ClientChannel import ClientChannel
from .WsAioChannelAssistant import WsAioChannelAssistant
from .WsAioClientConnector import WsAioClientConnector


class WsAioClient(ClientBase):

    def __init__(self, config: ClientConfig):
        super().__init__(config, WsAioChannelAssistant(config))
        self.client = None
        self.log = logger.opt()
        self.__loop = asyncio.get_event_loop()

    async def open(self) -> Session:
        client = WsAioClientConnector(self)
        self.log.info(f"open {self._config.get_url()}")
        self.client = await client.connect()
        channel: Channel = ClientChannel(self.client, client)
        return SessionDefault(channel)
