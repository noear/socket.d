import asyncio

from test.modelu.BaseTestCase import BaseTestCase

import time
from websockets.legacy.server import WebSocketServer

from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.module.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest, config_handler, send_and_subscribe_test
from loguru import logger


class TestCase01_client_send(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.server_session: WebSocketServer = None
        self.client_session: Session = None
        self.loop = asyncio.get_event_loop()

    async def _start(self):
        self.server: Server = SocketD.create_server(ServerConfig(self.schema).set_port(self.port))
        self.server_session: WebSocketServer = await self.server.config(config_handler).listen(
            SimpleListenerTest()).start()

        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(config_handler).open()
        await self.client_session.send_and_request("demo", StringEntity("test"), 100)

        start_time = time.monotonic()
        for _ in range(3):
            await self.client_session.send("demo", StringEntity("test"))

        await self.client_session.send_and_subscribe("demo", StringEntity("test"), send_and_subscribe_test, 100)
        end_time = time.monotonic()
        logger.info(f"Coroutine send took {(end_time - start_time) * 1000.0} monotonic to complete.")
        await asyncio.sleep(3)

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
