from abc import ABC, abstractmethod

from socketd.transport.core import Listener
from socketd.transport.core.Channel import Channel
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Message import Message


class Processor(ABC):

    @abstractmethod
    def set_listener(self, listener: Listener) -> None:
        pass

    @abstractmethod
    def on_receive(self, channel: Channel, frame:Frame):
        pass

    @abstractmethod
    def on_open(self, channel: ChannelInternal):
        pass

    @abstractmethod
    def on_message(self, channel: ChannelInternal, message:Message):
        pass

    @abstractmethod
    def on_close(self, channel: ChannelInternal):
        pass

    @abstractmethod
    def on_error(self, channel: ChannelInternal, error):
        pass
    @abstractmethod
    def do_close_notice(self, channel: ChannelInternal):
        pass
