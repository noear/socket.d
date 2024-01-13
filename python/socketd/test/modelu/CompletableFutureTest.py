import asyncio
import unittest

from loguru import logger

from socketd.transport.utils.CompletableFuture import CompletableFuture


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
