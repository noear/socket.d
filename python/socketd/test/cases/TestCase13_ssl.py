import asyncio
import ssl
import platform

from socketd import SocketD
from socketd.transport.client.ClientConfig import ClientConfig
from test.modelu.BaseTestCase import BaseTestCase

from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest
from loguru import logger

def is_mac_os():
    return 'Darwin' in platform.system()

def get_s_ssl():
    ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    ssl_context.check_hostname = False

    if is_mac_os():
        ssl_context.load_cert_chain(certfile=r"/Users/noear/WORK/work_github/noear/socketd/python/socketd/test/cases/ssl/server.crt",
                                    keyfile=r"/Users/noear/WORK/work_github/noear/socketd/python/socketd/test/cases/ssl/server.key")
    else:
        ssl_context.load_cert_chain(certfile=r"D:\java_items\socketd\python\socketd\test\cases\ssl\server.crt",
                                keyfile=r"D:\java_items\socketd\python\socketd\test\cases\ssl\server.key")
    return ssl_context


def get_c_ssl():
    ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
    ssl_context.verify_mode = ssl.CERT_REQUIRED  # 强制要求进行证书验证
    ssl_context.check_hostname = False
    # 添加 CA 证书路径
    if is_mac_os():
        ssl_context.load_cert_chain(certfile=r"/Users/noear/WORK/work_github/noear/socketd/python/socketd/test/cases/ssl/client.crt",
                                    keyfile=r"/Users/noear/WORK/work_github/noear/socketd/python/socketd/test/cases/ssl/client.key")
        ssl_context.load_verify_locations(r"/Users/noear/WORK/work_github/noear/socketd/python/socketd/test/cases/ssl/server.crt")
    else:
        ssl_context.load_cert_chain(certfile=r"D:\java_items\socketd\python\socketd\test\cases\ssl\client.crt",
                                keyfile=r"D:\java_items\socketd\python\socketd\test\cases\ssl\client.key")
        ssl_context.load_verify_locations(r"D:\java_items\socketd\python\socketd\test\cases\ssl\server.crt")

    return ssl_context


class TestCase13_ssl(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.client_session: Session = None
        self.loop = asyncio.get_event_loop()

    async def _start(self):
        def s_config_handler(config: ServerConfig | ClientConfig):
            config.idle_timeout(10000)
            # config.set_logger_level("DEBUG")
            config.ssl_context(get_s_ssl())

        s = SimpleListenerTest()
        self.server: Server = await (SocketD.create_server(ServerConfig("ws").port(self.port))
                               .config(s_config_handler).listen(s)
                               .start())
        await asyncio.sleep(1)
        serverUrl = "ws" + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"

        def c_config_handler(config: ServerConfig | ClientConfig):
            config.idle_timeout(10000)
            # config.set_logger_level("DEBUG")
            config.ssl_context(get_c_ssl())

        self.client_session: Session = await SocketD.create_client(serverUrl) \
            .config(c_config_handler).open()
        self.client_session.send_and_request("demo", StringEntity("test"), 100)

        for _ in range(100):
            self.client_session.send("demo", StringEntity("test"))

        await asyncio.sleep(5)
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
