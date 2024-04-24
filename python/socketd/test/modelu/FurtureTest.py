import asyncio
import sys
import time

import unittest
from threading import Thread

from socketd.utils.AsyncUtils import AsyncUtils
from loguru import logger

from socketd.utils.async_api.AtomicRefer import AtomicRefer
from test.uitls import calc_async_time


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
            t = Thread(target=lambda: AsyncUtils.thread_handler(loop, loop.create_task(_test())))
            t.start()
            for i in range(10):
                await asyncio.sleep(1)
                print(2)
            t.join()

        asyncio.run(_run())

    def test_call_later(self):
        """阻塞"""
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
            return True

        loop = asyncio.new_event_loop()
        t = Thread(target=_run, args=(loop,))
        t.start()
        Future = asyncio.run_coroutine_threadsafe(_handle(loop), loop)
        for _ in range(10):
            print(_)

    def test_loop1(self):
        logger.info("test_loop1")
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        top = AsyncUtils.run_forever(loop)
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
                top.cancel()  # 程序中止，遇到下一个await后续丢失

        logger.remove()
        for i in range(10):
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

            top = AsyncUtils.run_forever(loop)
            # top2 = AsyncUtil.run_forever(loop2)
            _time = asyncio.get_event_loop().time()
            for _ in range(50000):
                asyncio.run_coroutine_threadsafe(_hand(top), loop)
                # asyncio.run_coroutine_threadsafe(_hand(top2), loop2)
            await asyncio.sleep(0)
            logger.info(f"{asyncio.get_event_loop().time() - _time}")
            asyncio.run_coroutine_threadsafe(_hand(top), loop)
            # loop.stop()
            # top2.set_result(1)
            for _ in range(10):
                logger.info("1")
            logger.info(f"运行结束 {await num.get()}")

        asyncio.run(main())

    def test_task(self):
        async def function_A():
            print("Function A started at", time.strftime('%X'))
            await asyncio.sleep(1)
            print("Function A ended at", time.strftime('%X'))

        async def function_B():
            print("Function B started at", time.strftime('%X'))
            await asyncio.sleep(3)
            print("Function B ended at", time.strftime('%X'))

        @calc_async_time
        async def main():
            # 启动两个函数的协程
            task_A = asyncio.create_task(function_A())
            task_B = asyncio.create_task(function_B())
            print(" await ...")
            # 等待两个函数完成
            await asyncio.gather(task_A, task_B)

        asyncio.run(main())

    def test_warp(self):

        async def get(data):
            print(data)

        async def _main(func):
            await func(1)

        asyncio.run(_main(get))

    def test_send(self):
        async def send():
            await asyncio.sleep(2)
            a = yield
            print(a)
            yield

        async def _main():
            s = send()
            await anext(s, 1)
            await anext(s, 2)

        asyncio.run(_main())

    def testTask(self):
        """
        多线程混合运行异步函数
        :return:
        """
        loop = asyncio.new_event_loop()
        stop = AsyncUtils.run_forever(loop, daemon=True)

        async def task():
            num = AtomicRefer(0)

            async def send(_num):
                await asyncio.sleep(1)
                logger.info(1)
                async with _num as _n:
                    await _num.set(_n + 1)
                return 1

            tasks = []
            for t in range(1000):
                # asyncio.run_coroutine_threadsafe(send(num), loop)
                task = loop.create_task(send(num))
                tasks.append(task)
            f = asyncio.run_coroutine_threadsafe(asyncio.wait(tasks), loop)
            await asyncio.sleep(1)
            try:
                f.result()
            except TimeoutError as e:
                f.cancel()
                logger.error(e)
            loop.stop()
            logger.info("num : {}".format(await num.get()))
            assert await num.get() == 1000
            stop.set_result(True)

        asyncio.run(task())
