import asyncio
import sys

from loguru import logger

from websockets.legacy.server import Serve
from socketd.core.SocketD import SocketD
from socketd.core.config.ServerConfig import ServerConfig

from test.SimpleListenerTest import idGenerator, SimpleListenerTest

logger.remove()
logger.add(sys.stderr, level="ERROR")


def main():
    server = SocketD.create_server(ServerConfig("ws").setPort(7779))
    server_session: Serve = server.config(idGenerator).listen(
        SimpleListenerTest()).start()
    asyncio.get_event_loop().run_forever()


if __name__ == "__main__":
    main()
