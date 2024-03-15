from socketd.exception.SocketDExecption import SocketDChannelException
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Costants import Constants


class AssertsUtil(object):

    @staticmethod
    def assert_closed(channel: Channel):
        if channel is not None and channel.is_closed() > 0:
            raise SocketDChannelException(
                "This _channel is closed, sessionId=" + channel.get_session().get_session_id())

    @staticmethod
    def is_closed_and_end(channel: Channel) -> bool:
        return channel.is_closed() == Constants.CLOSE29_USER or channel.is_closed() == Constants.CLOSE28_OPEN_FAIL
