from abc import ABC, abstractmethod


class ClientConnector(ABC):
    @abstractmethod
    def heartbeatHandler(self):
        pass

    @abstractmethod
    def heartbeatInterval(self) -> int:
        pass

    @abstractmethod
    def autoReconnect(self):
        pass

    @abstractmethod
    async def connect(self):
        pass
