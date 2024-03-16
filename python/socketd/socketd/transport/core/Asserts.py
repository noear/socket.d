from socketd.exception.SocketDExecption import SocketDChannelException
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Costants import Constants

class Asserts:
    @staticmethod
    def assert_size(name: str, size: int, limitSize: int) -> None:
        if size > limitSize:
            buf = f"This message {name} size is out of limit {limitSize} ({size})"
            raise RuntimeError(buf)

    @staticmethod
    def assert_closed(self, channel: Channel):
        if channel and channel.isClosed() > 0:
            raise SocketDChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId())

    @staticmethod
    def is_closed_and_end(channel: Channel):
        return channel.isClosed() == Constants.CLOSE2009_USER or channel.isClosed() == Constants.CLOSE2008_OPEN_FAIL

    @staticmethod
    def assert_closed_and_end(channel: Channel):
        if channel and Asserts.is_closed_and_end(channel):
            raise SocketDChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId())

    @staticmethod
    def assert_null(name: str, val):
        if val == None:
            raise SocketDChannelException("The argument cannot be null: " + name)

    @staticmethod
    def assert_empty(name: str, val: str):
        if val == None or val.count() == 0:
            raise SocketDChannelException("The argument cannot be empty: " + name)