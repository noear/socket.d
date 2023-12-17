import uuid
from websockets.legacy.server import WebSocketServer

from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.config.ClientConfig import ClientConfig
from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.module.StringEntity import StringEntity
from socketd.core.sync_api.AtomicRefer import AtomicRefer
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest
from test.uitls import calc_async_time


class BaseTest:

    def __init__(self):
        self.__client_session: Session = None
        self.__server_session: WebSocketServer = None
        self.__server: Server = None
        self.__s_lock = AtomicRefer(0)
        self.__c_lock = AtomicRefer(0)

    @classmethod
    def s_config(cls, _config: ServerConfig) -> ServerConfig:
        _config.id_generator(uuid.uuid4)
        return _config

    @classmethod
    def c_config(cls, _config: ClientConfig) -> ClientConfig:
        _config.id_generator(uuid.uuid4)
        return _config

    async def start(self):
        self.__server: Server = SocketD.create_server(ServerConfig("ws").set_port(9999))
        self.__server_session: WebSocketServer = await self.__server.config(self.s_config).listen(
            SimpleListenerTest()).start()

        self.__client_session: Session = await SocketD.create_client("ws://127.0.0.1:9999") \
            .config(self.c_config).open()

    @calc_async_time
    async def send(self, count=100000):
        for _ in range(count):
            await self.__client_session.send("demo", StringEntity("test"))
            with self.__c_lock:
                self.__c_lock.set(self.__c_lock.get() + 1)

    @calc_async_time
    async def send_and_request(self, count=100000):
        for _ in range(count):
            await self.__client_session.send_and_request("demo", StringEntity("test"), 100)
            with self.__c_lock:
                self.__c_lock.set(self.__c_lock.get() + 1)

    @calc_async_time
    async def send_and_subscribe(self, count=100000):
        for _ in range(count):
            await self.__client_session.send_and_subscribe("demo", StringEntity("test"), lambda e: print(e), 100)
            with self.__c_lock:
                self.__c_lock.set(self.__c_lock.get() + 1)

    async def close(self):
        if self.__client_session:
            await self.__client_session.close()
        if self.__server_session:
            self.__server_session.close()
        await self.__server.stop()