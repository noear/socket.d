import asyncio
import time
from abc import ABC

from websockets.legacy.server import WebSocketServer
from loguru import logger
from pathlib import Path

from socketd.core.Listener import Listener
from socketd.core.module.Entity import EntityMetas
from socketd.core.module.FileEntity import FileEntity
from socketd.core.module.Message import Message
from test.modelu.BaseTestCase import BaseTestCase

from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.module.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import config_handler
from socketd.core.sync_api.AtomicRefer import AtomicRefer


class SimpleListenerTest(Listener, ABC):

    def __init__(self):
        self.message_counter = AtomicRefer(0)

    def on_open(self, session):
        pass

    async def on_message(self, session, message: Message):
        logger.debug(message)
        with self.message_counter:
            self.message_counter.set(self.message_counter.get() + 1)

        file_name = message.get_meta(EntityMetas.META_DATA_DISPOSITION_FILENAME)
        out_file_name = "./test"
        if file_name:
            logger.debug(f"file_name {file_name}")
            with open(out_file_name, "wb") as f:
                f.write(message.get_data_as_bytes())

        path = Path(out_file_name)
        assert path.exists()

    def on_close(self, session):
        pass

    def on_error(self, session, error):
        pass


class TestCase05_file(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server
        self.server_session: WebSocketServer
        self.client_session: Session
        self.loop = asyncio.get_event_loop()

    async def _start(self):
        self.server: Server = SocketD.create_server(ServerConfig(self.schema).set_port(self.port))
        _simple = SimpleListenerTest()
        _server = self.server.config(config_handler).listen(_simple)
        self.server_session: WebSocketServer = await _server.start()
        await asyncio.sleep(1)
        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(config_handler).open()
        try:
            with open(r"C:\Users\bai\Pictures\46c7a111437ea55469f1f5f5b35c3e55.mp4", "rb") as f:
                await self.client_session.send("/path?u=a&p=2", FileEntity(f.read(), "test"))
        except Exception as e:
            logger.error(e)
            raise e
        logger.info(
            f" message {_simple.message_counter.get()}")

    def start(self):
        super().start()
        self.loop.run_until_complete(self._start())
        time.sleep(2)

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
