from socketd.transport.core.Channel import Channel


class AssertsUtil(object):

    @staticmethod
    def assert_closed(channel: Channel):
        if channel is not None and channel.is_closed():
            raise Exception("This _channel is closed, sessionId=" + channel.get_session().get_session_id())
