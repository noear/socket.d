from typing import Callable, Optional

from socketd.transport.core.Entity import Reply
from socketd.transport.core.Costants import Constants
from socketd.transport.core.stream.StreamBase import StreamBase


class SubscribeStream(StreamBase):

    def __init__(self, sid: str, timeout):
        super().__init__(sid, Constants.DEMANDS_MULTIPLE, timeout)
        self.__isDone: Optional[bool] = None
        self.__doOnReply: Optional[Callable[[Reply], None]] = None

    def on_reply(self, reply):
        self.__isDone = reply.is_end()
        try:
            if self.__doOnReply:
                self.__doOnReply(reply)
        except Exception as e:
            self.on_error(e)

    def is_done(self):
        return self.__isDone

    def thenReply(self, __doOnReply: Callable[[Reply], None]):
        self.__doOnReply = __doOnReply
        return self
