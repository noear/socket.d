import asyncio
import functools
from typing import Generic, TypeVar, Callable

from loguru import logger

T = TypeVar('T')


class CompletableFuture(Generic[T]):

    def __init__(self, _future=None):
        if _future and not asyncio.iscoroutine(_future):
            logger.warning("{name}对象不是协程对象", name=_future.__name__)
            return
        self._future: asyncio.Task = asyncio.create_task(_future) if _future else asyncio.Future()
        self._lock = asyncio.Lock()

    def get(self, timeout):
        async def _get():
            await asyncio.wait_for(self._future, timeout)
            return self._future.result()

        return _get()

    def accept(self, result: T):
        if not self._future.done():
            self._future.set_result(result)

    def then_callback(self, _fn: Callable[[T], None], *args, **kwargs):
        self._future.add_done_callback(functools.partial(_fn, *args, **kwargs))

    def then_async_callback(self, _fn: Callable):
        def callback(fn: asyncio.Future):
            asyncio.run_coroutine_threadsafe(_fn(fn.result(), fn.exception()), asyncio.get_running_loop())

        self._future.add_done_callback(functools.partial(callback))

    async def then_success_callback(self, callback):
        try:
            result = await self._future
            await callback(result, None)
            return result
        except Exception as e:
            await callback(self._future.result(), e)

    def set_result(self, t: T):
        self._future.set_result(t)

    def set_e(self, e: Exception):
        self._future.set_exception(e)

    def done(self):
        self._future.done()

    def cancel(self):
        self._future.cancel()

    def get_result(self):
        return self._future.result()
