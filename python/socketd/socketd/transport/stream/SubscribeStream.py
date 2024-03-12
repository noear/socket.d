from typing import Callable, Optional

from socketd.transport.core.Entity import Reply
from socketd.transport.core.Costants import Constants
from socketd.transport.stream.StreamBase import StreamBase


class SubscribeStream(StreamBase):

    def __init__(self, sid: str, timeout):
        super().__init__(sid, Constants.DEMANDS_MULTIPLE, timeout)
        self.__isDone: Optional[bool] = None
        self.__doOnReply: Optional[Callable] = None

    async def on_reply(self, reply: Reply):
        self.__isDone = reply.is_end()
        try:
            if self.__doOnReply:
                await self.__doOnReply(reply)
        except Exception as e:
            self.on_error(e)

    def is_done(self):
        return self.__isDone

    def then_reply(self, __doOnReply: Callable):
        self.__doOnReply = __doOnReply
        return self
