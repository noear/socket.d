from typing import Optional, Callable

from socketd.transport.core.Costants import Constants
from socketd.transport.core.Message import MessageInternal
from socketd.transport.stream.SubscribeStream import SubscribeStream
from socketd.transport.stream.impl.StreamBase import StreamBase
from socketd.utils.RunUtils import RunUtils


class SubscribeStreamImpl(StreamBase, SubscribeStream):
    def __init__(self, sid: str, timeout):
        super().__init__(sid, Constants.DEMANDS_MULTIPLE, timeout)
        self.__isDone: bool = False
        self.__doOnReply: Callable[[MessageInternal], None] = None

    def is_done(self):
        return self.__isDone

    async def on_reply(self, reply: MessageInternal):
        self.__isDone = reply.is_end()
        try:
            if self.__doOnReply:
                await RunUtils.waitTry(self.__doOnReply(reply))
        except Exception as e:
            self.on_error(e)

    def then_reply(self, doOnReply: Callable[[MessageInternal], None]) -> SubscribeStream:
        self.__doOnReply = doOnReply
        return self

    def then_error(self, onError: Callable[[Exception], None]) -> SubscribeStream:
        self._then_error_do(onError)
        return self

    def then_progress(self, onProgress: Callable[[bool, int, int], None]) -> SubscribeStream:
        self._then_progress_do(onProgress)
        return self