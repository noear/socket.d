import asyncio

from socketd import SocketD
from test.modelu.BaseTestCase import BaseTestCase

from websockets.legacy.server import WebSocketServer

from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest, config_handler
from loguru import logger


class TestCase01_client_send(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.server_session: WebSocketServer = None
        self.client_session: Session = None
        self.loop = asyncio.new_event_loop()

    async def _start(self):
        s = SimpleListenerTest()
        self.server: Server = SocketD.create_server(ServerConfig(self.schema).port(self.port))
        self.server_session: Server = await self.server.config(config_handler).listen(s).start()
        await asyncio.sleep(1)
        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client_session: Session = await SocketD.create_client(serverUrl).config(config_handler).open()
        await self.client_session.send_and_request("demo", StringEntity("test"), 100)

        for _ in range(100):
            await self.client_session.send("demo", StringEntity("test"))

        await asyncio.sleep(2)
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
        self.loop.stop()

    def on_error(self):
        super().on_error()
