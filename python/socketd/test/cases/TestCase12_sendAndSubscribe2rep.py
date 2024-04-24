import asyncio

from socketd import SocketD
from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.core.Message import Message
from socketd.transport.stream import SubscribeStream
from socketd.utils.sync_api.AtomicRefer import AtomicRefer
from test.modelu.BaseTestCase import BaseTestCase

from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from socketd.transport.core.Listener import Listener

from loguru import logger


def config_handler(config: ServerConfig | ClientConfig):
    config.is_thread(False)
    config.idle_timeout(10000)
    config.logger_level("DEBUG")


class SimpleListenerTest(Listener):

    def __init__(self):
        self.server_counter = AtomicRefer(0)
        self.close_counter = AtomicRefer(0)
        self.message_counter = AtomicRefer(0)

    async def on_open(self, session):
        pass

    async def on_message(self, session, message: Message):
        with self.server_counter:
            self.server_counter.set(self.server_counter.get() + 1)
        logger.info(f"server::{message.data_as_string()} :: {message}")
        if message.is_subscribe():
            req: SubscribeStream = await session.send_and_subscribe("demo", StringEntity("今天不好"))

            async def _then_reply(entity):
                logger.info(f"server::res:: {entity}")
                await session.reply_end(message ,entity)
                self.message_counter.set(self.message_counter.get() + 1)

            req.then_reply(_then_reply)

    def on_close(self, session):
        logger.debug("客户端主动关闭了")
        with self.close_counter:
            self.close_counter.set(self.close_counter.get() + 1)

    def on_error(self, session, error):
        pass


class ClientListenerTest(Listener):

    async def on_open(self, session):
        pass

    def on_close(self, session):
        pass

    def on_error(self, session, error):
        logger.info(error)
        raise error

    def __init__(self):
        self.close_counter = AtomicRefer(0)
        self.message_counter = AtomicRefer(0)

    async def on_message(self, session: Session, message: Message):
        logger.info(f"client::  {message.data_as_string()} {message}")
        if message.is_subscribe():
            await session.reply_end(message, StringEntity("你好"))


class TestCase12_sendAndSubscribe2rep(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.client_session: Session = None
        self.loop = asyncio.get_event_loop()

    async def _start(self):
        s = SimpleListenerTest()
        self.server: Server = await (SocketD.create_server(ServerConfig(self.schema).port(self.port))
                               .config(config_handler)
                               .listen(s).start())

        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client = SocketD.create_client(serverUrl) \
            .listen(ClientListenerTest()) \
            .config(config_handler)
        self.client_session: Session = await self.client.open()
        await asyncio.sleep(1)

        async def send_and_subscribe_test(entity):
            logger.debug(f"c::subscribe::{entity.data_as_string()} {entity}")

        req: SubscribeStream = self.client_session.send_and_subscribe("demo", StringEntity("hi"), 100)
        req.then_reply(send_and_subscribe_test)
        await asyncio.sleep(3)

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
