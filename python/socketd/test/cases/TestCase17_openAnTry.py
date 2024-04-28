import asyncio

from socketd import SocketD
from test.modelu.BaseTestCase import BaseTestCase

from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest, config_handler, ClientListenerTest
from loguru import logger


class TestCase17_openAnTry(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.client_session: Session = None
        self.loop = asyncio.get_event_loop()

    async def _start(self):
        s = SimpleListenerTest()
        self.server: Server = await (SocketD.create_server(ServerConfig(self.schema).port(self.port))
                               .config(config_handler).listen(s)
                               .start())

        await asyncio.sleep(1)
        self.client_session: Session = await (SocketD.create_cluster_client(f"{self.schema}://127.0.0.1:{self.port}/",
                                                                           f"{self.schema}://127.0.0.1:{self.port}/")
                                              .listen(ClientListenerTest())
                                              .config(config_handler)
                                              .open())

        self.client_session.send("demo", StringEntity("test"))

        await asyncio.sleep(2)
        logger.info(
            f" message {s.server_counter.get()}")

    def start(self):
        super().start()
        self.loop.run_until_complete(self._start())

    async def _stop(self):
        if self.client_session:
            await self.client_session.close()

        if self.server:
            await self.server.stop()

    def stop(self):
        super().stop()

        self.loop.run_until_complete(self._stop())

    def on_error(self):
        super().on_error()
