import asyncio
from typing import Optional

from loguru import logger

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Message import Message
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.core.listener.EventListener import EventListener
from socketd.utils.sync_api.AtomicRefer import AtomicRefer
from test.modelu.BaseTestCase import BaseTestCase

from socketd.transport.core.Session import Session
from socketd import SocketD
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import config_handler


class SimpleListenerTest(Listener):

    def __init__(self):
        self.server_counter = AtomicRefer(0)
        self.close_counter = AtomicRefer(0)
        self.message_counter = AtomicRefer(0)

    async def on_open(self, session):
        logger.info("server on_open")
        pass

    async def on_message(self, session: Session, message: Message):
        with self.server_counter:
            self.server_counter.set(self.server_counter.get() + 1)
            logger.info(f":: {message}")
        if self.server_counter.get() == 1:
            try:
                await session.close()
                pass
            except Exception as e:
                logger.error(e)

    def on_close(self, session):
        logger.debug("客户端主动关闭了")

    def on_error(self, session, error):
        pass


class TestCase10_serverCloseReconnect(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.top: Optional[asyncio.Future]  = None
        self.server: Optional[Server] = None
        self.client_session: Optional[Session] = None
        self.loop = asyncio.get_event_loop()
        self._simple = SimpleListenerTest()

    async def _start(self):
        self.server: Server = await (SocketD.create_server(ServerConfig(self.schema).port(self.port)).config(config_handler)
                               .listen(self._simple)
                               .start())

        def do_on_close(session: Session):
            logger.debug("do_on_close")
            # if asyncio.run(self._simple.message_counter.get()) == 1:
            asyncio.run_coroutine_threadsafe(session.reconnect(), loop=asyncio.get_event_loop())

        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(config_handler).listen(EventListener().do_on_close(do_on_close)).open()

        self.client_session.send("/demo", StringEntity("test"))

        await asyncio.sleep(10)
        self.client_session.send("/demo", StringEntity("test"))
        self.client_session.send("/demo", StringEntity("test"))
        await asyncio.sleep(1)
        logger.info(f"counter {self._simple.server_counter.get()} ")

    def start(self):
        super().start()
        self.loop.run_until_complete(self._start())
        logger.info(
            f" message {self._simple.message_counter.get()}")

    async def _stop(self):
        if self.client_session:
            await self.client_session.close()
        if self.top:
            self.top.set_result(0)

        if self.server:
            await self.server.stop()

    def stop(self):
        super().stop()
        self.loop.run_until_complete(self._stop())

    def on_error(self):
        super().on_error()
