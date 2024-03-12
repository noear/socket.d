from abc import ABC
from typing import TypeVar, Union, Generic

from .Client import ClientInternal
from .ClientConfig import ClientConfig
from .ClientConnector import ClientConnector
from socketd.transport.core.ChannelInternal import ChannelInternal

T = TypeVar("T", bound=Union[ChannelInternal])


class ClientConnectorBase(ClientConnector, Generic[T], ABC):

    def __init__(self, client: ClientInternal):
        self.client: ClientInternal = client

    def autoReconnect(self):
        return self.client.get_config().is_auto_reconnect()

    def get_config(self) -> ClientConfig:
        return self.client.get_config()
