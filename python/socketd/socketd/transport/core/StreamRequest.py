from socketd.core.module.Message import Message
from socketd.transport.core.CompletableFuture import CompletableFuture
from socketd.transport.core.StreamBase import StreamBase


class StreamRequest(StreamBase):
    def __init__(self, sid, timeout, future):
        self.future: CompletableFuture = future
        super().__init__(sid, timeout)

    def is_single(self):
        return True

    def is_done(self):
        return self.future.done()

    def timeout(self):
        return self.timeout

    def on_accept(self, message: Message, onError):
        self.future.set_result(message)