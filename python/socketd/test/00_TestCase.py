import asyncio
import unittest
import sys
from loguru import logger

from test.modelu.BaseTest import BaseTest


class TestCase(unittest.TestCase):

    count = 10000
    timeout = 30

    def __init__(self, *args, **kwargs):
        logger.remove()
        logger.add(sys.stderr, level="INFO")
        super().__init__(*args, **kwargs)

    def test_send(self):
        test = BaseTest()
        loop = asyncio.get_event_loop()
        try:
            loop.run_until_complete(test.start())
            loop.run_until_complete(test.send(TestCase.count))
            loop.run_until_complete(test.send_and_request(TestCase.count))
            loop.run_until_complete(test.send_and_subscribe(TestCase.count))
        except Exception as e:
            pass
        finally:
            test.close()


