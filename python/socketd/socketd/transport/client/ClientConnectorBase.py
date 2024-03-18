from abc import ABC

from .Client import ClientInternal
from .ClientConfig import ClientConfig
from .ClientConnector import ClientConnector


class ClientConnectorBase(ClientConnector, ABC):

    def __init__(self, client: ClientInternal):
        self.client: ClientInternal = client

    def get_config(self) -> ClientConfig:
        return self.client.get_config()

    def auto_reconnect(self):
        return self.client.get_config().is_auto_reconnect()



