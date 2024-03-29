import asyncio

from socketd import SocketD
from test.modelu.BaseTestCase import BaseTestCase

from websockets.legacy.server import WebSocketServer

from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest, config_handler
from loguru import logger


class TestCase16_connect(BaseTestCase):

    def __init__(self, schema, port):
        super().__init__(schema, port)
        self.server: Server = None
        self.server_session: WebSocketServer = None
        self.loop = asyncio.get_event_loop()
        self.client_session_queue = asyncio.Queue()

    async def _start(self):
        s = SimpleListenerTest()
        self.server: Server = SocketD.create_server(ServerConfig(self.schema).port(self.port))
        self.server_session: WebSocketServer = await self.server \
            .config(config_handler) \
            .listen(s) \
            .start()
        await asyncio.sleep(1)
        serverUrl = self.schema + "://127.0.0.1:" + str(self.port) + "/path?u=a&p=2"

        async def _main():
            for _ in range(10):
                client_session: Session = await SocketD.create_client(serverUrl) \
                    .config(config_handler).open()
                await client_session.send_and_request("demo", StringEntity("test"), 100)

                for _ in range(3):
                    await client_session.send("demo", StringEntity("test"))
                await self.client_session_queue.put(client_session)

        # 并发连接到服务器 10 * 10
        await asyncio.gather(*[_main() for _ in range(10)])
        await asyncio.sleep(30)
        logger.info(
            f" message {s.server_counter.get()}")

    def start(self):
        super().start()
        self.loop.run_until_complete(self._start())

    async def _stop(self):
        if not self.client_session_queue.empty():
            for _ in range(self.client_session_queue.qsize()):
                client_session = await self.client_session_queue.get()
                await client_session.close()

        if self.server_session:
            self.server_session.close()
        if self.server:
            await self.server.stop()

    def stop(self):
        super().stop()

        self.loop.run_until_complete(self._stop())

    def on_error(self):
        super().on_error()
