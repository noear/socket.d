import asyncio
import uuid
import time
from websockets.legacy.server import WebSocketServer

from socketd.transport.core.Session import Session
from socketd.transport.core.SocketD import SocketD
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.Entity import Entity
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest
from test.uitls import calc_async_time
from loguru import logger




def idGenerator(config):
    return config.id_generator(uuid.uuid4)


def send_and_subscribe_test(e: Entity):
    print(e)


@calc_async_time
async def application_test():
    server: Server = SocketD.create_server(ServerConfig("ws").set_port(9999))
    server_session: WebSocketServer = await server.config(idGenerator).listen(
        SimpleListenerTest()).start()
    await asyncio.sleep(1)
    client_session: Session = await SocketD.create_client("ws://127.0.0.1:9999") \
        .config(idGenerator).open()

    start_time = time.monotonic()
    for _ in range(1):
        await client_session.send("demo", StringEntity("test.png"))
        # await client_session.send_and_request("demo", StringEntity("test.png"), 100)
        # await client_session.send_and_subscribe("demo", StringEntity("test.png"), send_and_subscribe_test, 100)
    end_time = time.monotonic()
    logger.info(f"Coroutine send took {(end_time - start_time) * 1000.0} monotonic to complete.")
    await client_session.close()
    server_session.close()
    # await server.stop()


if __name__ == "__main__":
    # logger.remove()
    # logger.add(sys.stderr, level="INFO")
    asyncio.get_event_loop().run_until_complete(application_test())
