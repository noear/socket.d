from typing import Callable

from socketd.transport.core.Entity import Entity
from socketd.transport.core.Message import Message
from socketd.transport.core.stream.StreamBase import StreamBase


class StreamSubscribe(StreamBase):

    def __init__(self, sid: str, timeout: int, future):
        super().__init__(sid, timeout)
        self.future: Callable[[Entity], None] = future
        self.timeout = timeout

    def is_single(self):
        return False

    def is_done(self):
        return False

    def on_accept(self, message: Message, onError) -> None:
        try:
            self.future(message)
        except Exception as e:
            onError(e)
