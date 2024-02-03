import asyncio
from asyncio import Future
from typing import Callable, Optional, List, Awaitable, Any

from socketd import SocketD
from socketd.cluster.ClusterClientSession import ClusterClientSession
from socketd.transport.client.Client import Client, ClientInternal
from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.core import Listener
from socketd.transport.core.Session import Session
from socketd.transport.core.impl.HeartbeatHandlerDefault import HeartbeatHandler


class ClusterClient(Client):

    def __init__(self, *serverUrls):
        self._listener = None
        self._serverUrls: Optional[tuple[Any, ...]] = serverUrls
        self._heartbeatHandler: Optional[HeartbeatHandler] = None
        self._configHandler: Optional[Callable[[ClientConfig], ClientConfig]] = None

    def heartbeatHandler(self, handler: HeartbeatHandler) -> 'Client':
        self._heartbeatHandler = handler
        return self

    def config(self, consumer: Callable[[ClientConfig], ClientConfig]) -> 'Client':
        self._configHandler = consumer
        return self

    def listen(self, listener: Listener) -> 'Client':
        self._listener = listener
        return self

    async def _open(self, is_throw):
        sessions: List[Session] = []
        channelExecutor = None
        for urls in self._serverUrls:
            for url in urls.split(","):
                client: ClientInternal = SocketD.create_client(url)

                if self._listener:
                    client.listen(self._listener)

                if self._configHandler:
                    client.config(self._configHandler)

                if self._heartbeatHandler:
                    client.heartbeatHandler(self._heartbeatHandler)

                if channelExecutor is None:
                    channelExecutor = client.get_config().get_executor()
                else:
                    client.get_config().executor(channelExecutor)

                sessions.extend(await asyncio.gather(*[client.openOrThrow() if is_throw else client.open()]))
        return ClusterClientSession(sessions)

    def open(self) -> Awaitable[ClientSession]:
        return self._open(False)

    def openOrThrow(self) -> Awaitable[ClientSession]:
        return self._open(True)
