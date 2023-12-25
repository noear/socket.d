import asyncio
from typing import Coroutine, Any


class AsyncUtil:

    @staticmethod
    def thread_handler(_loop, fn: asyncio.Task):
        """
        静态方法 thread_handler 用于在指定的事件循环中运行一个协程函数。

        参数:
            _loop (EventLoop): 事件循环对象，用于控制协程的执行。
            fn (Coroutine): 需要运行的协程函数。
        """
        asyncio.set_event_loop(_loop)
        _loop.run_until_complete(fn)

