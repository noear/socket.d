import asyncio
from typing import Callable
from socketd.exception.SocketDExecption import SocketDTimeoutException, SocketDException
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Entity import Reply
from socketd.transport.stream.StreamBase import StreamBase
from socketd.transport.utils.CompletableFuture import CompletableFuture


class RequestStream(StreamBase):

    def __init__(self, sid: str, timeout: int):
        super().__init__(sid, Constants.DEMANDS_SINGLE, timeout)
        self._future: CompletableFuture = CompletableFuture()

    def is_done(self):
        return self._future.done()

    def __await__(self):
        try:
            return self._future.get(self.timeout())
        except asyncio.TimeoutError as _e:
            raise SocketDTimeoutException(f"Request reply timeout>{self.timeout()}  sid={self.get_sid()}")
        except Exception as _e:
            raise SocketDException(f"Request failed, sid= sid={self.get_sid()} {str(_e)}")

    async def await_result(self):
        if self._future.done():
            return self._future.get_result()
        return await self.__await__()

    async def on_reply(self, message: Reply):
        return await self._future.set_result(message)

    def then_reply(self, onReply: Callable[[Reply], None]):
        self._future.then_callback(onReply)
