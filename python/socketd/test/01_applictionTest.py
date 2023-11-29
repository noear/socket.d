import asyncio
import uuid
import sys

import nest_asyncio
from websockets.legacy.server import Serve

from socketd.core.Buffer import Buffer
from socketd.core.Costants import Flag
from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.module.Frame import Frame
from socketd.core.module.MessageDefault import MessageDefault
from socketd.core.module.StringEntity import StringEntity
from socketd.transport.CodecByteBuffer import CodecByteBuffer
from test.SimpleListenerTest import SimpleListenerTest
from test.uitls import calc_time
from loguru import logger


nest_asyncio.apply()
logger.remove()
logger.add(sys.stderr, level="ERROR")


def main():
    b = Buffer()
    b.put_int(Flag.Message.value)
    print(b.getvalue())
    b.flip()
    print(b.getvalue())
    print(b.size())

    code = CodecByteBuffer(ServerConfig("ws"))
    b1 = code.write(Frame(Flag.Message,
                          MessageDefault().set_sid("1700534070000000001")
                          .set_flag(Flag.Subscribe)
                          .set_topic("tcp-java://127.0.0.1:9386/path?u=a&p=2")
                          .set_entity(StringEntity("test"))
                          ),
                    lambda l: Buffer())
    print(b1.getvalue())
    b1.seek(0)
    b2 = code.read(b1)
    print(b2)
    b1.close()


def idGenerator(config):
    return config.id_generator(uuid.uuid4)

@calc_time
async def appliction_test():
    server = SocketD.create_server(ServerConfig("ws").setPort(9999))
    server_session: Serve = server.config(idGenerator).listen(
        SimpleListenerTest()).start()

    await asyncio.sleep(3)

    client_session: Session = SocketD.create_client("ws://127.0.0.1:9999") \
        .config(idGenerator).open()

    for _ in range(100000):
        await client_session.send("demo", StringEntity("test"))
    # await client_session.send("demo2", StringEntity("test"))
    await client_session.close()
    asyncio.get_event_loop().run_forever()


if __name__ == "__main__":
    # main()
    asyncio.run(appliction_test())


