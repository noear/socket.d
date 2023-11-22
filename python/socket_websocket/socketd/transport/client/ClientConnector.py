from abc import ABC, abstractmethod


class ClientConnector(ABC):
    @abstractmethod
    def heartbeatHandler(self):
        pass

    @abstractmethod
    def heartbeatInterval(self):
        pass

    @abstractmethod
    def autoReconnect(self):
        pass

    @abstractmethod
    def connect(self):
        pass
