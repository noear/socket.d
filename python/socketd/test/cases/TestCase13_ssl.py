import asyncio
import ssl

from socketd import SocketD
from socketd.transport.client.ClientConfig import ClientConfig
from test.modelu.BaseTestCase import BaseTestCase

from websockets.legacy.server import WebSocketServer

from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest
from loguru import logger


def get_s_ssl():
    ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
    ssl_context.load_cert_chain(certfile=r"D:\java_items\socketd\python\socketd\test\cases\ssl\test.pem",
                                keyfile=r"D:\java_items\socketd\python\socketd\test\cases\ssl\test.key")
    return ssl_context


def get_c_ssl():
    ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    ssl_context.check_hostname = False
    ssl_context.load_verify_locations(r"D:\java_items\socketd\python\socketd\test\cases\ssl\test.pem")
    return ssl_context


class TestCase13_ssl(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.server_session: WebSocketServer = None
        self.client_session: Session = None
        self.loop = asyncio.get_event_loop()

    async def _start(self):
        s = SimpleListenerTest()
        self.server: Server = SocketD.create_server(ServerConfig(self.schema).port(self.port))

        def s_config_handler(config: ServerConfig | ClientConfig):
            config.idle_timeout(10000)
            # config.set_logger_level("DEBUG")
            config.ssl_context(get_s_ssl())

        self.server_session: WebSocketServer = await self.server.config(s_config_handler).listen(
            s).start()
        await asyncio.sleep(1)
        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"

        def c_config_handler(config: ServerConfig | ClientConfig):
            config.idle_timeout(10000)
            # config.set_logger_level("DEBUG")
            config.ssl_context(get_c_ssl())

        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(c_config_handler).open()
        await self.client_session.send_and_request("demo", StringEntity("test"), 100)

        for _ in range(100):
            await self.client_session.send("demo", StringEntity("test"))

        await asyncio.sleep(5)
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

    def on_error(self):
        super().on_error()
