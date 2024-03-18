import asyncio
import unittest
from asyncio import Queue
from concurrent.futures import ThreadPoolExecutor

from socketd.transport.core.codec import Buffer
from socketd.transport.core.Costants import Flags
from socketd.transport.core.entity.MessageBuilder import MessageBuilder
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.Frame import Frame
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.core.codec.CodecDefault import CodecDefault
from test.uitls import calc_time


class CodeTest(unittest.TestCase):

    def test01(self):
        code = CodecDefault(ServerConfig("ws"))
        b1 = code.write(Frame(Flags.Message,
                              MessageBuilder().sid("1700534070000000001")
                              .entity(StringEntity("test"))
                              .event("demo").build()
                              ),
                        lambda l: Buffer(100))
        print(f"size {b1.size()} pos = {b1.tell()}")
        b1.seek(0)
        b2 = code.read(b1)
        print(f"size {b1.size()} pos = {b1.tell()}")
        print(b1.getvalue())
        print(b2)
        b1.close()

    @calc_time
    def test(self):
        b = Buffer(limit=100)
        print(b.size())
        b.put_int(Flags.Message)
        print(b.size())
        print(b.getvalue())
        b.flip()
        print(b.getvalue())
        print(b.size())
        code = CodecDefault(ServerConfig("ws"))
        for _ in range(100000):
            b1 = code.write(Frame(Flags.Message,
                                  MessageBuilder().sid("1700534070000000001").entity(StringEntity("test")).build()
                                  ),
                            lambda l: Buffer(100))
            b1.seek(0)
            b2 = code.read(b1)
            b1.close()
        # for _ in range(100000):
        #     b = Buffer(limit=5)
        #     b.put_int(1)
        #     b.close()
        b.close()

    @calc_time
    def test_async(self):
        loop = asyncio.get_event_loop()
        code = CodecDefault(ServerConfig("ws"))

        async def _test():
            tasks = []
            t = ThreadPoolExecutor()
            queue = Queue()

            async def _run1():
                for _ in range(100):
                    b1 = await loop.run_in_executor(t, lambda _code: _code.write(Frame(Flags.Message,
                                                                                       MessageBuilder().sid("1700534070000000001")
                                                                                       .entity(StringEntity("test")).build()
                                                                                       ),
                                                                                 lambda l: Buffer(100)), (code))
                    b1.seek(0)
                    await queue.put(b1)
                print(queue.qsize())

            async def _run2():
                while True:
                    try:
                        b1 = await queue.get()
                        b2 = await loop.run_in_executor(t, lambda: code.read(b1), )
                    except asyncio.TimeoutError:
                        print("timeout")
                        break

            tasks.append(_run1())
            tasks.append(_run2())

            await asyncio.gather(*tasks)
            t.shutdown()

        loop.run_until_complete(_test())
        # for _ in range(100000):
        #     b = Buffer(limit=5)
        #     b.put_int(1)
        #     b.close()
