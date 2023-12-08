import asyncio
from typing import Generic, TypeVar

T = TypeVar('T')


class CompletableFuture(Generic[T]):

    def __init__(self, _future=None):
        self._future: asyncio.Future = _future if _future else asyncio.Future()

    def get(self, timeout):
        async def _get():
            await asyncio.wait_for(self._future, timeout)
            return self._future.result()
        return _get()

    def accept(self, result: T, onError):
        self._future.set_result(result)

    def then_async(self, f: asyncio.Future):
        self._future = asyncio.ensure_future(f)

    def set_result(self, t: T):
        self._future.set_result(t)

    def done(self):
        self._future.done()

    def cancel(self):
        self._future.cancel()
