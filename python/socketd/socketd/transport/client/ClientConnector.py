from abc import ABC, abstractmethod

from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.core.ChannelInternal import ChannelInternal


class ClientConnector(ABC):

    @abstractmethod
    def get_config(self) -> ClientConfig:
        ...

    @abstractmethod
    def auto_reconnect(self) -> bool:
        ...

    @abstractmethod
    async def connect(self) -> ChannelInternal:
        ...

    @abstractmethod
    async def close(self):
        ...