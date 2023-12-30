import asyncio
from abc import ABC

from websockets.legacy.server import WebSocketServer
from loguru import logger

from socketd.transport.core.Listener import Listener
from socketd.transport.core.entity.Message import Message
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.core.sync_api.AtomicRefer import AtomicRefer
from test.modelu.BaseTestCase import BaseTestCase

from socketd.transport.core.Session import Session
from socketd.transport.core.SocketD import SocketD
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import config_handler


class SimpleListenerTest(Listener, ABC):

    def __init__(self):
        self.server_counter = AtomicRefer(0)
        self.close_counter = AtomicRefer(0)
        self.message_counter = AtomicRefer(0)

    async def on_open(self, session):
        pass

    async def on_message(self, session: Session, message: Message):
        with self.server_counter:
            self.server_counter.set(self.server_counter.get() + 1)
            logger.info(f":: {message}")
            if self.server_counter.get() == 1:
                await session.close()

    def on_close(self, session):
        logger.debug("客户端主动关闭了")
        with self.close_counter:
            self.close_counter.set(self.close_counter.get() + 1)

    def on_error(self, session, error):
        pass


class TestCase10_serverCloseReconnect(BaseTestCase):
    """
    todo 未完成测试
    """

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server
        self.server_session: WebSocketServer
        self.client_session: Session
        self.loop = asyncio.get_event_loop()
        self._simple = SimpleListenerTest()

    async def _start(self):
        self.server: Server = SocketD.create_server(ServerConfig(self.schema).set_port(self.port))
        _server = self.server.config(config_handler).listen(self._simple)
        self.server_session: WebSocketServer = await _server.start()
        await asyncio.sleep(1)
        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(config_handler).open()

        await self.client_session.send("/path?u=a&p=2", StringEntity("test"))
        await asyncio.sleep(10)

    def start(self):
        super().start()
        self.loop.run_until_complete(self._start())
        logger.info(
            f" message {self._simple.message_counter.get()}")

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
