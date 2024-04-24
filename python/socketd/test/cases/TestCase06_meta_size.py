import asyncio

from socketd.exception.SocketDExecption import SocketDChannelException
from test.modelu.BaseTestCase import BaseTestCase

import time

from socketd.transport.core.Session import Session
from socketd import SocketD
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest, config_handler
from loguru import logger


class TestCase06_meta_size(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.client_session: Session = None
        self.loop = asyncio.get_event_loop()
        self.listener = SimpleListenerTest()

    async def _start(self):
        self.server: Server = await (SocketD.create_server(ServerConfig(self.schema).port(self.port))
                               .config(config_handler)
                               .listen(self.listener)
                               .start())
        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(config_handler).open()
        __meta = "*=1&" * 50000
        start_time = time.monotonic()
        try:
            self.client_session.send("demo", StringEntity("test").meta_string_set(__meta))
        except SocketDChannelException as s:
            logger.error(str(s))
        self.client_session.send("demo", StringEntity("test").meta_put("name", "bai"))
        end_time = time.monotonic()
        logger.info(f"Coroutine send took {(end_time - start_time) * 1000.0} monotonic to complete.")
        await asyncio.sleep(3)
        logger.info(f" message {self.listener.message_counter.get()}")

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
