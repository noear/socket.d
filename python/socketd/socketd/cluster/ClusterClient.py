import asyncio
from typing import List

from socketd import SocketD
from socketd.cluster.ClusterClientSession import ClusterClientSession
from socketd.transport.client.Client import Client, ClientInternal
from socketd.transport.client.ClientConfigHandler import ClientConfigHandler
from socketd.transport.client.ClientConnectHandler import ClientConnectHandler
from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.core import Listener
from socketd.transport.core.Session import Session
from socketd.transport.client.ClientHeartbeatHandler import ClientHeartbeatHandler


class ClusterClient(Client):

    def __init__(self, serverUrls:tuple[str]):
        self.__serverUrls:tuple[str] = serverUrls

        self.__connectHandler: ClientConnectHandler = None
        self.__heartbeatHandler: ClientHeartbeatHandler = None
        self.__configHandler: ClientConfigHandler = None

        self.__listener = None

    def connect_handler(self, connectHandler: ClientConnectHandler)  -> Client:
        self.__connectHandler = connectHandler
        return self

    def heartbeat_handler(self, heartbeatHandler: ClientHeartbeatHandler) -> Client:
        self.__heartbeatHandler = heartbeatHandler
        return self

    def config(self, configHandler: ClientConfigHandler) -> Client:
        self.__configHandler = configHandler
        return self

    def listen(self, listener: Listener) -> Client:
        self.__listener = listener
        return self

    async def open(self) -> ClientSession:
        return await self._open_do(False)

    async def open_or_throw(self) -> ClientSession:
        return await self._open_do(True)

    async def _open_do(self, is_throw):
        sessions: List[Session] = []
        exchangeExecutor = None
        for urls in self.__serverUrls:
            for url in urls.split(","):
                client: ClientInternal = SocketD.create_client(url)

                if self.__listener:
                    client.listen(self.__listener)

                if self.__configHandler:
                    client.config(self.__configHandler)

                if self.__connectHandler:
                    client.connect_handler(self.__connectHandler)

                if self.__heartbeatHandler:
                    client.heartbeat_handler(self.__heartbeatHandler)

                if exchangeExecutor is None:
                    exchangeExecutor = client.get_config().get_exchange_executor()
                else:
                    client.get_config().exchange_executor(exchangeExecutor)

                sesssion = await (client.open_or_throw() if is_throw else client.open())
                sessions.append(sesssion)
        return ClusterClientSession(sessions)


