
import asyncio
import uuid

from socketd import SocketD
from socketd.transport.client.ClientConfig import ClientConfig
from test.modelu.BaseTestCase import BaseTestCase

from websockets.legacy.server import WebSocketServer

from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest, config_handler
from loguru import logger


def c_config_handler(config: ServerConfig | ClientConfig):
    config.is_thread(True)
    config.idle_timeout(10000)
    config.auto_reconnect(False)
    # config.set_logger_level("DEBUG")


class TestCase14_timeout(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.server_session: WebSocketServer = None
        self.client_session: Session = None
        self.loop = asyncio.get_event_loop()

    async def _start(self):
        s = SimpleListenerTest()
        self.server: Server = SocketD.create_server(ServerConfig(self.schema).port(self.port))
        self.server_session: WebSocketServer = await self.server.config(config_handler).listen(
            s).start()
        await asyncio.sleep(1)
        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(c_config_handler).open()
        await asyncio.sleep(100)
        await self.client_session.send("demo", StringEntity("test"))
        await asyncio.sleep(5)
        logger.info(
            f" message {s.server_counter.get()}")

    def start(self):
        super().start()
        self.loop.run_until_complete(self._start())

    async def _stop(self):
        if self.client_session:
            await self.client_session.close()

        if self.server_session:
            self.server_session.close()
        if self.server:
            await self.server.stop()

    def stop(self):
        super().stop()

        self.loop.run_until_complete(self._stop())

    def on_error(self):
        super().on_error()
