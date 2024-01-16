import asyncio
import sys
import time

import unittest
from threading import Thread

from socketd.transport.utils.AsyncUtil import AsyncUtil
from loguru import logger

from socketd.transport.utils.async_api.AtomicRefer import AtomicRefer


class FutureTest(unittest.TestCase):

    def __init__(self, methodName: str = ...):
        super().__init__(methodName)
        self.loop = asyncio.get_event_loop()

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
            t = Thread(target=lambda: AsyncUtil.thread_handler(loop, loop.create_task(_test())))
            t.start()
            for i in range(10):
                await asyncio.sleep(1)
                print(2)
            t.join()

        asyncio.run(_run())

    def test_call_later(self):
        results = []

        def callback(arg):
            results.append(arg)

        self.loop.call_later(0.1, callback, 'hello world')
        self.loop.run_forever()
        self.loop.stop()
        self.assertEqual(results, ['hello world'])
        print(results)

    def test_loop(self):
        def _run(_loop: asyncio.AbstractEventLoop):
            asyncio.set_event_loop(_loop)
            _loop.run_forever()

        async def _handle(_loop):
            for _ in range(10):
                print("_")
            _loop.stop()

        loop = asyncio.new_event_loop()
        t = Thread(target=_run, args=(loop,))
        t.start()
        asyncio.run_coroutine_threadsafe(_handle(loop), loop)
        for _ in range(10):
            print("1")
            time.sleep(0.1)

    def test_loop1(self):
        logger.info("test_loop1")
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        top = AsyncUtil.run_forever(loop)
        num = AtomicRefer(0)

        async def _run(s, _top):
            nonlocal num
            await num.set(await num.get() + 1)
            await asyncio.sleep(0)
            for _ in range(10):
                logger.debug(f"{s} {_}")
            if _top.done():
                if task := asyncio.current_task(loop):
                    task.cancel()  # 取消任务
            await asyncio.sleep(0)
            logger.debug(f"{s} run结束")

        async def main(_top):
            await _run("one", _top)
            if not top.done():
                top.set_result(0)  # 程序中止，遇到下一个await后续丢失

        logger.remove()
        for i in range(100000):
            # 讲异步任务提交到事件循环中
            asyncio.run_coroutine_threadsafe(_run("two", top), loop)
            # loop.run_until_complete(_run("two", top))
            # asyncio.run_coroutine_threadsafe(_run("三", top), loop)
            # asyncio.run_coroutine_threadsafe(_run("四", top), loop)
        asyncio.run(main(top))
        for _ in range(10):
            logger.info("1")

        print(asyncio.run(num.get()))
        logger.info("loop结束")

        # asyncio.run_coroutine_threadsafe(_run(), loop) # error

    def test_loop2(self):
        logger.remove()
        logger.add(sys.stderr, level="INFO")

        async def main():
            loop = asyncio.new_event_loop()
            num = AtomicRefer(0)

            async def _hand(_top):
                await num.set(await num.get() + 1)
                for _ in range(10):
                    logger.debug(f"_")

            top = AsyncUtil.run_forever(loop)
            # top2 = AsyncUtil.run_forever(loop2)
            _time = asyncio.get_event_loop().time()
            for _ in range(50000):
                asyncio.run_coroutine_threadsafe(_hand(top), loop)
                # asyncio.run_coroutine_threadsafe(_hand(top2), loop2)
            await asyncio.sleep(0)
            logger.info(f"{asyncio.get_event_loop().time() - _time}")
            asyncio.run_coroutine_threadsafe(_hand(top), loop)
            loop.stop()
            # top2.set_result(1)
            for _ in range(10):
                logger.info("1")
            logger.info(f"运行结束 {await num.get()}")

        asyncio.run(main())

