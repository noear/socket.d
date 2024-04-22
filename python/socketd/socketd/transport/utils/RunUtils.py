import asyncio


class RunUtils:
    @staticmethod
    def taskTry(task):
        if task:
            asyncio.create_task(task)