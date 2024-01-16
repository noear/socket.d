import asyncio
import uuid

from socketd.SocketD import SocketD
from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.core.Message import Message
from socketd.transport.core.stream.RequestStream import RequestStream
from socketd.transport.utils.sync_api.AtomicRefer import AtomicRefer
from test.modelu.BaseTestCase import BaseTestCase

from websockets.legacy.server import WebSocketServer

from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from socketd.transport.core.Listener import Listener

from loguru import logger


def config_handler(config: ServerConfig | ClientConfig) -> ServerConfig | ClientConfig:
    config.set_is_thread(True)
    config.set_idle_timeout(10)
    config.set_logger_level("DEBUG")
    config.id_generator(uuid.uuid4)
    return config


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
        logger.info(f"server::{message.get_data_as_string()} :: {message}")
        if message.is_request():
            req: RequestStream = await session.send_and_request("demo", StringEntity("今天不好"), 100)
            # todo 开启单独线程后，在open确认连接后，会停留10s(线程可见性不佳)，但是可以解决线程阻塞问题
            # await asyncio.sleep(1)
            entity = await req.await_result()
            await session.reply_end(message, entity)
            logger.info(f"server::res::: {entity}")
            with self.message_counter:
                self.message_counter.set(self.message_counter.get() + 1)

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
        logger.info(f"client: {message} {message.get_data_as_string()}")
        if message.is_request():
            await session.reply_end(message, StringEntity("很好"))


class TestCase11_sendAndRequest2rep(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.client = None
        self.server_session: WebSocketServer = None
        self.client_session: Session = None
        self.loop = asyncio.get_event_loop()

    async def _start(self):
        s = SimpleListenerTest()
        self.server: Server = SocketD.create_server(ServerConfig(self.schema).set_port(self.port))
        self.server_session: WebSocketServer = await self.server.config(config_handler).listen(
            s).start()

        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"
        self.client = SocketD.create_client(serverUrl) \
            .listen(ClientListenerTest()) \
            .config(config_handler)
        self.client_session: Session = await self.client.open()
        await asyncio.sleep(1)
        req: RequestStream = await self.client_session.send_and_request("demo", StringEntity("你好"), 100)
        entity = await req.await_result()
        logger.info(f"c: res{entity} {entity.get_data_as_string()}")
        await asyncio.sleep(1)

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
