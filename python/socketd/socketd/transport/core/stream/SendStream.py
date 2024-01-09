from socketd.transport.core.Costants import Constants
from socketd.transport.core.stream.StreamBase import StreamBase


class SendStream(StreamBase):

    def __init__(self, sid: str):
        super().__init__(sid, Constants.DEMANDS_ZERO, 0)

    def on_reply(self, reply):
        pass

    def is_done(self):
        return True
