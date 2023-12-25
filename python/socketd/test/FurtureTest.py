import asyncio

import unittest


class FutureTest(unittest.TestCase):

    def test_wait(self):
        async def _wait():
            top = asyncio.Future()
            top.set_result(0)
            await top

        asyncio.run(_wait())
