
from socketd.transport.core.ChannelInternal import ChannelInternal


class ClientHandshakeResult:

    def __init__(self, __channel: ChannelInternal, __throwable: Exception | None):
        self._channel = __channel
        self._throwable = __throwable

    def get_channel(self):
        return self._channel

    def get_throwable(self):
        return self._throwable
