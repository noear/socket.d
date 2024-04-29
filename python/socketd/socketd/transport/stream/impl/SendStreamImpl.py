from typing import Callable

from socketd.transport.core.Costants import Constants
from socketd.transport.stream.SendStream import SendStream
from socketd.transport.stream.impl.StreamBase import StreamBase


class SendStreamImpl(StreamBase, SendStream):
    def __init__(self, sid: str):
        super().__init__(sid, Constants.DEMANDS_ZERO, 0)

    def is_done(self):
        return True

    async def on_reply(self, reply):
        ...

    def then_error(self, onError: Callable[[Exception], None]) -> 'SendStream':
        super()._then_error_do(onError)
        return self


    def then_progress(self, onProgress: Callable[[bool, int, int], None]) -> 'SendStream':
        super().then_progress(onProgress)
        return self
