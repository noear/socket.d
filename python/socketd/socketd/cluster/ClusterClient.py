from asyncio import Future
from typing import Callable, Optional

from socketd.transport.client.Client import Client
from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.core import Listener
from socketd.transport.core.Session import Session
from socketd.transport.core.impl.HeartbeatHandlerDefault import HeartbeatHandler


class ClusterClient(Client):

    def __init__(self, *serverUrls):
        self._listener = None
        self._serverUrls = serverUrls
        self._heartbeatHandler: Optional[HeartbeatHandler] = None
        self._consumer: Callable[[ClientConfig], ClientConfig] = None

    def heartbeatHandler(self, handler: HeartbeatHandler) -> 'Client':
        self._heartbeatHandler = handler
        return self

    def config(self, consumer: Callable[[ClientConfig], ClientConfig]) -> 'Client':
        self._consumer = consumer
        return self

    def listen(self, listener: Listener) -> 'Client':
        self._listener = listener
        raise self


    def open(self) -> Session | Future:
        pass

    def openOrThrow(self) -> Session | Future:
        pass
