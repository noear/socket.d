from abc import ABC, abstractmethod


class ClientConnector(ABC):
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
