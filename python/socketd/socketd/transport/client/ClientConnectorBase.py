from abc import ABC

from .Client import Client, ClientInternal
from .ClientConfig import ClientConfig
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

    def get_config(self) -> ClientConfig:
        return self.client.get_config()

