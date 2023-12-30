import asyncio
import unittest

from socketd.transport.core.CompletableFuture import CompletableFuture


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
