import asyncio

import unittest
from threading import Thread

from socketd.transport.utils.AsyncUtil import AsyncUtil


class FutureTest(unittest.TestCase):

    def test_wait(self):
        async def _wait():
            top = asyncio.Future()
            top.set_result(0)
            await top

        asyncio.run(_wait())



    def test_thread(self):
        async def _test():
            for i in range(10):
                await asyncio.sleep(1)
                print(1)

        async def _run():
            loop = asyncio.new_event_loop()
            t = Thread(target=lambda : AsyncUtil.thread_handler(loop, loop.create_task(_test())))
            t.start()
            for i in range(10):
                await asyncio.sleep(1)
                print(2)
            t.join()
        asyncio.run(_run())
