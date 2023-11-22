import asyncio
from concurrent.futures import ThreadPoolExecutor, as_completed, wait, ALL_COMPLETED
from common.globalconfig import global_config

as_completed = as_completed
wait = wait
ALL_COMPLETED = ALL_COMPLETED

ThreadPool = ThreadPoolExecutor()
# 生产最大线程10个
if global_config.active == "prod":
    ThreadPool._max_workers = 10


class AsyncUtils:

    @staticmethod
    def run(func):
        return asyncio.run(func)

    @staticmethod
    def run_coroutine(coro):
        """运行一个协程并返回结果"""
        loop = asyncio.get_event_loop()
        task = loop.create_task(coro)
        return task

    @staticmethod
    async def async_core(core):
        return core()

    @staticmethod
    def run_futures(coro):
        """运行一个协程并返回结果"""
        loop = asyncio.get_event_loop()
        task = loop.create_task(coro)
        loop.run_until_complete(asyncio.wait(task))
        loop.close()
        return task.result()

    @staticmethod
    async def to_thread(func, *args, **kwargs):
        """将一个阻塞型函数转换成协程任务，使用线程池异步执行"""
        loop = asyncio.get_running_loop()
        return await loop.run_in_executor(ThreadPool, lambda: func(*args, **kwargs))

    @staticmethod
    async def gather_concurrent(coros, limit=10):
        """并发执行多个协程任务，并限制同时执行的数量"""

        async def worker(semaphore, coro):
            async with semaphore:
                return await coro
        if limit < 0:
            return await asyncio.gather(*coros)
        semaphore = asyncio.Semaphore(limit)
        tasks = [worker(semaphore, coro) for coro in coros]
        return await asyncio.gather(*tasks)

    @staticmethod
    async def run_with_timeout(coro, timeout=None):
        """运行一个协程任务，并设置超时时间"""
        task = asyncio.ensure_future(coro)
        done, pending = await asyncio.wait([task], timeout=timeout)
        if task in done:
            return task.result()
        else:
            raise asyncio.TimeoutError()

    @staticmethod
    async def repeat_task(coro, interval):
        """重复运行一个协程任务，每隔一定时间间隔执行一次"""
        while True:
            await coro
            await asyncio.sleep(interval)

    @staticmethod
    async def sleep_until(timestamp):
        """将当前协程挂起一段时间，直到指定的时间点"""
        now = asyncio.get_event_loop().time()
        if timestamp > now:
            await asyncio.sleep(timestamp - now)
        else:
            raise ValueError("timestamp must be in the future")
