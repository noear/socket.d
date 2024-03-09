import asyncio
import sys

from loguru import logger

from websockets.legacy.server import WebSocketServer
from socketd import SocketD
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerConfig import ServerConfig

from test.modelu.SimpleListenerTest import config_handler

# logger.remove()
# logger.add(sys.stderr, level="DEBUG")

from test.cases.TestCase11_sendAndRequest2rep import SimpleListenerTest


async def main():
    server = SocketD.create_server(ServerConfig("tcp").port(7779))
    server_session: Server = await server.config(config_handler).listen(
        SimpleListenerTest()).start()
    await asyncio.Future()
    # server_session.close()


if __name__ == "__main__":
    asyncio.run(main())
