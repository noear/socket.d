from abc import ABC, abstractmethod

from socketd.transport.core.Channel import Channel


class Processor(ABC):

    @abstractmethod
    def set_listener(self, listener):
        pass

    @abstractmethod
    def on_receive(self, channel: Channel, frame):
        pass

    @abstractmethod
    async def on_open(self, channel: Channel):
        pass

    @abstractmethod
    async def on_message(self, channel: Channel, message):
        pass

    @abstractmethod
    def on_close(self, channel: Channel):
        pass

    @abstractmethod
    def on_error(self, channel: Channel, error):
        pass
