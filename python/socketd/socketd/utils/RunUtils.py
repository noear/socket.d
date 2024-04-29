import asyncio


class RunUtils:
    @staticmethod
    def taskTry(task):
        if task: #反回对象是异步时，以任务运行
            asyncio.create_task(task)

    @staticmethod
    async def waitTry(task):
        if task: #反回对象是异步时，以等待运行
            await task