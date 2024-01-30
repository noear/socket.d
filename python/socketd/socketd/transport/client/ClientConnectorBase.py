from abc import ABC

from .Client import Client, ClientInternal
from .ClientConnector import ClientConnector


class ClientConnectorBase(ClientConnector, ABC):

    def __init__(self, client: ClientInternal):
        self.client: ClientInternal = client

    def heartbeatHandler(self):
        return self.client.get_heartbeatHandler()

    def heartbeatInterval(self):
        return self.client.get_heartbeatInterval()

    def autoReconnect(self):
        return self.client.get_config().is_auto_reconnect()

