import asyncio
import uuid
import sys
import time
from websockets.legacy.server import Serve, WebSocketServer

from socketd.core.Buffer import Buffer
from socketd.core.Costants import Flag
from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.module.Frame import Frame
from socketd.core.module.MessageDefault import MessageDefault
from socketd.core.module.StringEntity import StringEntity
from socketd.transport.CodecByteBuffer import CodecByteBuffer
from socketd.transport.server.Server import Server
from test.SimpleListenerTest import SimpleListenerTest
from test.uitls import calc_time, calc_async_time
from loguru import logger


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


@calc_async_time
async def application_test():
    server: Server = SocketD.create_server(ServerConfig("ws").set_port(9999))
    server_session: WebSocketServer = await server.config(idGenerator).listen(
        SimpleListenerTest()).start()

    client_session: Session = await SocketD.create_client("ws://127.0.0.1:9999") \
        .config(idGenerator).open()

    start_time = time.monotonic()
    for _ in range(100000):
        await client_session.send("demo", StringEntity("test"))
    end_time = time.monotonic()
    logger.info(f"Coroutine send took {(end_time - start_time) * 1000.0} monotonic to complete.")
    await client_session.close()
    server_session.close()
    # await server.stop()


if __name__ == "__main__":
    logger.remove()
    logger.add(sys.stderr, level="ERROR")
    asyncio.get_event_loop().run_until_complete(application_test())
