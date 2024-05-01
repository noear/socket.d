import asyncio
import unittest
from concurrent.futures import ThreadPoolExecutor

from loguru import logger

from socketd.utils.AsyncUtils import AsyncUtils
from socketd.utils.CompletableFuture import CompletableFuture


class CompletableFutureTest(unittest.TestCase):

    def test(self):
        async def _test():
            c = CompletableFuture()
            c.accept("a", None)
            data = await c.get(10)
            print(data)

        asyncio.run(_test())

    def test_get(self):
        async def _test():
            c = CompletableFuture()
            data = c.get(10)
            c.accept("a", None)
            print(await data)

        asyncio.run(_test())

    def test_call(self):

        async def callback(result, e):
            logger.debug(result)

        async def _test():
            c = CompletableFuture()
            c.accept("a")
            data = await c.then_success_callback(callback)
            print(await c.get(100))

        asyncio.run(_test())

    def test_thread_wait(self):
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        c = CompletableFuture()

        async def _test(future: CompletableFuture):
            await future.set_result(1)

        async def _main():
            with ThreadPoolExecutor() as executor:
                loop = asyncio.get_running_loop()
                AsyncUtils.thread_loop(c.get(100), pool=executor)
                await loop.run_in_executor(executor, lambda c: asyncio.run(_test(c)), c)
        asyncio.run(_main())

    def test_get_func(self):

        async def _run():
            await asyncio.sleep(1)
            logger.info("run")
        async def _test():
            c = CompletableFuture(_run())
            data = asyncio.run_coroutine_threadsafe(c.get(10), asyncio.get_event_loop())
            await asyncio.sleep(1)
        asyncio.run(_test())
