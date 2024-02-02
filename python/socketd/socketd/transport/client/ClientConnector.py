from abc import ABC, abstractmethod

from socketd.transport.client.ClientConfig import ClientConfig


class ClientConnector(ABC):

    @abstractmethod
    def get_config(self) -> ClientConfig:
        ...

    @abstractmethod
    def heartbeatHandler(self):
        ...

    @abstractmethod
    def heartbeatInterval(self) -> int:
        ...

    @abstractmethod
    def autoReconnect(self):
        ...

    @abstractmethod
    async def connect(self):
        ...

    async def close(self):
        ...
