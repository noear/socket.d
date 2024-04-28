import asyncio
import functools
from threading import Lock
from typing import Generic, TypeVar, Callable

from socketd.utils.LogConfig import log

T = TypeVar('T')


class CompletableFuture(Generic[T]):

    def __init__(self, _future=None, loop=None):
        if loop is None:
            loop = asyncio.get_running_loop()
        if _future and not asyncio.iscoroutine(_future):
            log.warning("{name} invalid coroutine object", name=_future.__name__)
            return
        self._future: asyncio.Task = loop.create_task(_future) if _future else loop.create_future()
        self._lock = Lock()

    def get(self, timeout:float):
        with self._lock:
            async def _get():
                await asyncio.wait_for(self._future, timeout)
                return self._future.result()
        return _get()

    def accept(self, result: T):
        with self._lock:
            if not self._future.done():
                self._future.set_result(result)

    def then_callback(self, _fn: Callable[[T], None], *args, **kwargs):
        self._future.add_done_callback(functools.partial(_fn, *args, **kwargs))

    def then_async_callback(self, _fn):
        def callback(fn: asyncio.Future):
            asyncio.run_coroutine_threadsafe(_fn(fn.result(), fn.exception()), asyncio.get_running_loop())
        self._future.add_done_callback(functools.partial(callback))

    def then_success_callback(self, _fn):
        def callback(fn: asyncio.Future):
            _fn.send(None)
            _fn.send((fn.result(), fn.exception()))
        self._future.add_done_callback(functools.partial(callback))

    async def set_result(self, t: T):
        with self._lock:
            self._future.set_result(t)
        await self._future

    def set_e(self, e: Exception):
        self._future.set_exception(e)

    def done(self):
        self._future.done()

    def cancel(self):
        self._future.cancel()

    def get_result(self):
        return self._future.result()
