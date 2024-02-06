import asyncio
import unittest
from asyncio import Queue
from concurrent.futures import ThreadPoolExecutor
from socketd.transport.core.Codec import Codec, CodecReader, CodecWriter

from socketd.transport.core.codec.Buffer import Buffer
from socketd.transport.core.Costants import Flag
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.Frame import Frame
from socketd.transport.core.entity.MessageDefault import MessageDefault
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.core.codec.CodecByteBuffer import CodecByteBuffer, ByteBufferCodecWriter, ByteBufferCodecReader
from test.uitls import calc_time


class CodeTest(unittest.TestCase):

    def test01(self):
        code = CodecByteBuffer(ServerConfig("ws"))
        f1 = Frame(Flag.Message,
                   MessageDefault().set_sid("1700534070000000001")
                   .set_entity(StringEntity("test"))
                   .set_event("demo")
                   )
        print(f1)
        b1: CodecWriter = code.write(f1,
                                     lambda l: ByteBufferCodecWriter(Buffer(100)))
        print(b1.get_buffer().getvalue())
        b2 = code.read(ByteBufferCodecReader(b1.get_buffer()))
        print(b2)
        b1.close()

    @calc_time
    def test(self):
        b = Buffer(limit=100)
        print(b.size())
        b.put_int(Flag.Message)
        print(b.size())
        print(b.getvalue())
        b.flip()
        print(b.getvalue())
        print(b.size())
        code = CodecByteBuffer(ServerConfig("ws"))
        for _ in range(100000):
            b1 = code.write(Frame(Flag.Message,
                                  MessageDefault().set_sid("1700534070000000001")
                                  .set_entity(StringEntity("test"))
                                  ),
                            lambda l: ByteBufferCodecWriter(Buffer(100)))
            b2 = code.read(ByteBufferCodecReader(b1.get_buffer()))
            b1.close()
        # for _ in range(100000):
        #     b = Buffer(limit=5)
        #     b.put_int(1)
        #     b.close()
        b.close()

    @calc_time
    def test_async(self):
        loop = asyncio.get_event_loop()
        code = CodecByteBuffer(ServerConfig("ws"))

        async def _test():
            tasks = []
            t = ThreadPoolExecutor()
            queue = Queue()

            async def _run1():
                for _ in range(100):
                    b1 = await loop.run_in_executor(t, lambda _code: _code.write(Frame(Flag.Message,
                                                                                       MessageDefault().set_sid(
                                                                                           "1700534070000000001")
                                                                                       .set_entity(StringEntity("test"))
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
