import asyncio

from loguru import logger

from socketd.transport.core.ChannelSupporter import ChannelSupporter
from socketd.transport.client.ClientBase import ClientBase
from socketd.transport.client.ClientConfig import ClientConfig
from .WsAioChannelAssistant import WsAioChannelAssistant
from .WsAioClientConnector import WsAioClientConnector


class WsAioClient(ClientBase, ChannelSupporter):

    def __init__(self, config: ClientConfig):
        ClientBase.__init__(self, config, WsAioChannelAssistant(config))
        self.client = None
        self.log = logger.opt()

    def create_connector(self) -> WsAioClientConnector:
        return WsAioClientConnector(self)

