import asyncio

from socketd.transport.core.ChannelSupporter import ChannelSupporter
from socketd.transport.client.ClientBase import ClientBase
from socketd.transport.client.ClientConfig import ClientConfig

from .TcpAioClientConnector import TcpAioClientConnector
from .TcpAIOChannelAssistant import TcpAIOChannelAssistant


class TcpAioClient(ClientBase, ChannelSupporter):

    def __init__(self, client_config: ClientConfig):
        ClientBase.__init__(self, client_config, TcpAIOChannelAssistant(client_config, asyncio.get_event_loop()))

    def create_connector(self) -> TcpAioClientConnector:
        return TcpAioClientConnector(self)
