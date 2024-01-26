import logging

from socketd.transport.stream.StreamManger import StreamManger, StreamInternal

logger = logging.getLogger(__name__)


class StreamMangerDefault(StreamManger):
    def __init__(self, config):
        self.config = config
        self.stream_map:[str, StreamInternal] = {}

    def get_stream(self, sid):
        return self.stream_map.get(sid)

    def add_stream(self, sid, stream):
        if stream.demands() == 0:
            # Zero demands, do not add
            return

        self.stream_map[sid] = stream

        # Add stream timeout handling as a backup insurance
        stream_timeout = stream.timeout() if stream.timeout() > 0 else self.config.get_stream_timeout()
        if stream_timeout > 0:
            stream.insurance_start(self, stream_timeout)

    def remove_stream(self, sid):
        stream: StreamInternal = self.stream_map.pop(sid, None)
        if stream:
            stream.insurance_cancel()
            logger.debug(f"{self.config.get_role_name()} stream removed, sid={sid}")
