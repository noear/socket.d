import asyncio

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Message import Message
from socketd.transport.core.impl.LogConfig import log
from socketd.transport.utils.sync_api.AtomicRefer import AtomicRefer
from test.modelu.BaseTestCase import BaseTestCase

from websockets.legacy.server import WebSocketServer

from socketd.transport.core.Session import Session
from socketd import SocketD
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import config_handler


class SimpleListenerTest(Listener):

    def __init__(self):
        self.message_counter = AtomicRefer(0)

    async def on_open(self, session: Session):
        params = session.param("auth")
        if params != "root":
            await session.close()

    async def on_message(self, session, message: Message):
        with self.message_counter:
            self.message_counter.set(self.message_counter.get() + 1)
        log.info("message = {message}", message=message)
        if message.is_request():
            await session.reply_end(message, StringEntity("ok test"))
        elif message.is_subscribe():
            await session.reply(message, StringEntity("reply"))
            await session.reply_end(message, StringEntity("ok test"))

    def on_close(self, session):
        log.debug("客户端主动关闭了")

    def on_error(self, session, error):
        pass


class TestCase07_url_auth(BaseTestCase):

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
        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?auth=root"
        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(config_handler).open()
        try:
            await self.client_session.send("demo", StringEntity("root").meta_put("name", "root"))
            await self.client_session.close()
            serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?auth=123"
            self.client_session: Session = await SocketD.create_client(serverUrl) \
                .config(config_handler).open()

            await self.client_session.send("demo", StringEntity("test").meta_put("name", "bai"))
        except Exception as e:
            log.error(e)
        log.info(f" message {s.message_counter.get()}")

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
        self.loop.run_until_complete(self._stop())
        super().stop()

    def on_error(self):
        super().on_error()
