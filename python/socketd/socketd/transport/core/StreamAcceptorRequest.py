
from socketd.transport.core.CompletableFuture import CompletableFuture
from socketd.transport.core.StreamAcceptorBase import StreamAcceptorBase


class StreamAcceptorRequest(StreamAcceptorBase):
    def __init__(self, future, timeout):
        self.future: CompletableFuture = future
        self.timeout = timeout

    def is_single(self):
        return True

    def is_done(self):
        return self.future.done()

    def timeout(self):
        return self.timeout

    def accept(self, message, onError):
        self.future.set_result(message)