from abc import ABC
from typing import Coroutine, Callable

from socketd.transport.core.Costants import Constants
from socketd.transport.core.Entity import Reply
from socketd.transport.core.stream.StreamBase import StreamBase
from socketd.transport.utils.CompletableFuture import CompletableFuture


class RequestStream(StreamBase):

    def __init__(self, sid: str, timeout: int):
        super().__init__(sid, Constants.DEMANDS_SINGLE, timeout)
        self._future: CompletableFuture = CompletableFuture()

    def is_done(self):
        return self._future.done()

    def __await__(self):
        return self._future.get(self.timeout())

    def await_result(self):
        return self.__await__()

    async def on_reply(self, message: Reply):
        return self._future.set_result(message)

    def then_reply(self, onReply: Callable[[Reply], None]):
        self._future.then_callback(onReply)
