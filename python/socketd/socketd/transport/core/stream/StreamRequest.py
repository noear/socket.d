from socketd.transport.core.Message import Message
from socketd.transport.core.stream.StreamBase import StreamBase
from socketd.transport.utils.CompletableFuture import CompletableFuture


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