import unittest
import asyncio

from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd.transport.utils.async_api.AtomicRefer import AtomicRefer
from socketd.transport.utils.sync_api import AtomicRefer as AtomicRefer_
from test.uitls import calc_time
from loguru import logger
from concurrent.futures import ThreadPoolExecutor


class Demo:

    @staticmethod
    async def add(a: AtomicRefer):
        async with a:
            await a.set(await a.get() + 1)

    @staticmethod
    async def add2(a: AtomicRefer, num):
        for i in range(num):
            await Demo.add(a)

    @staticmethod
    def add3(a: AtomicRefer):
        loop = asyncio.get_event_loop()
        loop.run_until_complete(Demo.add(a))

    @staticmethod
    def add4(a: AtomicRefer_):
        with a:
            a.set(a.get() + 1)

    @staticmethod
    async def add5(a: AtomicRefer_):
        with a:
            a.set(a.get() + 1)


@calc_time
async def main():
    a = AtomicRefer(10)
    loop = asyncio.new_event_loop()
    future = AsyncUtil.run_forever(loop)
    async with a as t:
        logger.debug(t)
        # tasks = [add2(a, 1000) for _ in range(10)]
        # for i in tasks: # 15ms
        #     await i
        # await asyncio.gather(*[add2(a, 2000) for _ in range(5)]) # 16ms
        # await asyncio.gather(*[add(a) for _ in range(100000)]) # 60
        # await AsyncUtils.gather_concurrent([add(a) for _ in range(10000)], limit=10)  # 70
        # [await a.set(await a.get() + 1) for _ in range(10000)] # 16
        # await asyncio.gather(*[asyncio.create_task(add(a)) for _ in range(10000)]) # 40-60
        # for _ in range(10000): # 16ms
        #     await add(a)
        asyncio.run_coroutine_threadsafe(Demo.add2(a, 10), loop)
    future.set_result(1)
    logger.debug(await a.get())


class Test(unittest.TestCase):

    @calc_time
    def test_thread_lock(self):
        with ThreadPoolExecutor(max_workers=10) as t:
            a = AtomicRefer_(10)
            futures = [t.submit(Demo.add4, a) for _ in range(100000)]
            for i in futures:
                result = i.result()
            logger.debug(a.get())

    @calc_time
    def test_async_lock(self):
        async def _test_async_lock():
            a = AtomicRefer(10)
            await asyncio.gather(*[Demo.add(a) for _ in range(100000)])
            logger.debug(await a.get())
        asyncio.run(_test_async_lock())

    @calc_time
    def test_async_thread_lock(self):
        # 线程中使用异步锁, 贼慢 13s
        loop = asyncio.get_event_loop()
        with ThreadPoolExecutor(max_workers=10) as t:
            a = AtomicRefer(10)
            futures = [t.submit(lambda x:asyncio.run(Demo.add(x)), a) for _ in range(100000)]
            for i in futures:
                result = i.result()
            logger.debug(loop.run_until_complete(a.get()))

    @calc_time
    def test_thread_async_lock(self):
        async def _test_async_lock():
            a = AtomicRefer_(10)
            await asyncio.gather(*[Demo.add5(a) for _ in range(100000)])
            logger.debug(a.get())

        asyncio.run(_test_async_lock())