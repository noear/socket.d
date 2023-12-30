
from socketd.transport.core import Listener


class PipelineListener(Listener):

    def __init__(self):
        self._deque: list[Listener] = []

    def prev(self, listener: Listener) -> 'PipelineListener':
        self._deque.insert(0, listener)
        return self

    def next(self, listener: Listener) -> 'PipelineListener':
        self._deque.append(listener)
        return self

    def size(self) -> int:
        return self._deque.__len__()


