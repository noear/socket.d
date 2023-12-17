import asyncio
from asyncio.coroutines import iscoroutine
from typing import Generic, TypeVar

from loguru import logger

T = TypeVar('T')


class CompletableFuture(Generic[T]):

    def __init__(self, _future=None):
        if _future and not asyncio.iscoroutine(_future):
            logger.warning("{name}对象不是协程对象", name=_future.__name__)
            return
        self._future: asyncio.Task = asyncio.create_task(_future) if _future else asyncio.Future()

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
