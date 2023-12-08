from abc import ABC
from .ClientConnector import ClientConnector
from .ClientBase import ClientBase


class ClientConnectorBase(ClientConnector, ABC):

    def __init__(self, client: ClientBase):
        self.client = client

    def heartbeatHandler(self):
        return self.client.get_heartbeatHandler()

    def heartbeatInterval(self):
        return self.client.get_heartbeatInterval()

    def autoReconnect(self):
        return self.client.get_config().is_auto_reconnect()
