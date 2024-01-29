from abc import ABC, abstractmethod
from asyncio import Future

from socketd.transport.client.Client import ClientInternal
from socketd.transport.client.ClientChannel import ClientChannel
from socketd.transport.client.ClientConnector import ClientConnector
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Session import Session
from socketd.transport.core.impl.HeartbeatHandlerDefault import HeartbeatHandler
from socketd.transport.core.impl.ProcessorDefault import ProcessorDefault
from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.core.impl.SessionDefault import SessionDefault

from loguru import logger


class ClientBase(ClientInternal, ABC):

    def __init__(self, client_config: ClientConfig, assistant):
        self._processor = ProcessorDefault()
        self._heartbeat_handler = None
        self._config: ClientConfig = client_config
        self._assistant = assistant

    def get_assistant(self):
        return self._assistant

    def get_heartbeatInterval(self) -> int:
        return self._config.get_heartbeat_interval()

    def get_processor(self):
        return self._processor

    def heartbeatHandler(self, handler):
        if handler is not None:
            self._heartbeat_handler = handler
        return self

    def get_heartbeatHandler(self) -> HeartbeatHandler:
        return self._heartbeat_handler

    def get_config(self):
        return self._config

    def config(self, consumer):
        consumer(self._config)
        return self

    def process(self, processor):
        if processor is not None:
            self._processor = processor
        return self

    def listen(self, listener):
        self._processor.set_listener(listener)
        return self

    async def open(self) -> Session | Future:
        connector: ClientConnector = self.create_connector()
        channel0: ChannelInternal = await connector.connect()
        clientChannel = ClientChannel(channel0, connector)
        clientChannel.set_handshake(channel0.get_handshake())
        session: Session = SessionDefault(clientChannel)
        channel0.set_session(session)
        logger.info(f"Socket.D client successfully connected: {self._config.get_link_url()}")
        return session

    async def openOrThow(self) -> Session:
        ...

    @abstractmethod
    def create_connector(self):
        ...
