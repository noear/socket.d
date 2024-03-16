import asyncio
from typing import Optional, List, Awaitable, Any

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

    def __init__(self, *serverUrls):
        self._serverUrls: Optional[tuple[Any, ...]] = serverUrls

        self._connectHandler: ClientConnectHandler = None
        self._heartbeatHandler: ClientHeartbeatHandler = None
        self._configHandler: ClientConfigHandler = None

        self._listener = None

    def connectHandler(self, connectHandler: ClientConnectHandler)  -> Client:
        self._connectHandler = connectHandler
        return self

    def heartbeatHandler(self, heartbeatHandler: ClientHeartbeatHandler) -> Client:
        self._heartbeatHandler = heartbeatHandler
        return self

    def config(self, configHandler: ClientConfigHandler) -> Client:
        self._configHandler = configHandler
        return self

    def listen(self, listener: Listener) -> Client:
        self._listener = listener
        return self

    def open(self) -> Awaitable[ClientSession]:
        return self._open_do(False)

    def openOrThrow(self) -> Awaitable[ClientSession]:
        return self._open_do(True)

    async def _open_do(self, is_throw):
        sessions: List[Session] = []
        exchangeExecutor = None
        for urls in self._serverUrls:
            for url in urls.split(","):
                client: ClientInternal = SocketD.create_client(url)

                if self._listener:
                    client.listen(self._listener)

                if self._configHandler:
                    client.config(self._configHandler)

                if self._connectHandler:
                    client.connectHandler(self._connectHandler)

                if self._heartbeatHandler:
                    client.heartbeatHandler(self._heartbeatHandler)

                if exchangeExecutor is None:
                    exchangeExecutor = client.get_config().get_executor()
                else:
                    client.get_config().executor(exchangeExecutor)

                sessions.extend(await asyncio.gather(*[client.openOrThrow() if is_throw else client.open()]))
        return ClusterClientSession(sessions)


