from abc import ABC, abstractmethod
from typing import TypeVar, Callable

from socketd.transport.core import Listener
from socketd.transport.core.Channel import Channel
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Frame import Frame
from socketd.transport.stream.Stream import StreamInternal

S = TypeVar("S")

class Processor(ABC):

    @abstractmethod
    def set_listener(self, listener: Listener) -> None:
        pass

    @abstractmethod
    def send_frame(self, channel: ChannelInternal, frame: Frame, channelAssistant: ChannelAssistant[S], target: S):
        pass

    @abstractmethod
    def reve_frame(self, channel: Channel, frame: Frame):
        pass

    @abstractmethod
    def on_open(self, channel: ChannelInternal):
        pass

    @abstractmethod
    def on_message(self, channel: ChannelInternal, frame: Frame):
        pass

    @abstractmethod
    def on_reply(self, channel: ChannelInternal, frame: Frame, stream: StreamInternal) -> None:
        ...

    @abstractmethod
    def on_close(self, channel: ChannelInternal):
        pass

    @abstractmethod
    def on_error(self, channel: ChannelInternal, error):
        pass
    @abstractmethod
    def do_close_notice(self, channel: ChannelInternal):
        pass
