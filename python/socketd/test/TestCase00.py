import asyncio
import unittest
import sys
from loguru import logger

from test.modelu.BaseTest import BaseTest


class TestCase00(unittest.TestCase):

    count = 100000
    timeout = 30

    def __init__(self, *args, **kwargs):
        logger.remove()
        logger.add(sys.stderr, level="INFO")
        super().__init__(*args, **kwargs)

    def test_send(self):
        test = BaseTest()
        loop = asyncio.new_event_loop()
        try:
            async def _run():
                await test.start()
                await test.send(TestCase00.count)
                await test.send_and_request(TestCase00.count)
                await test.send_and_subscribe(TestCase00.count)
            loop.run_until_complete(_run())
        except Exception as e:
            pass
        finally:
            loop.run_until_complete(test.close())


