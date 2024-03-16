from abc import ABC, abstractmethod
from typing import Awaitable

from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.client.Client import ClientInternal
from socketd.transport.client.ClientChannel import ClientChannel
from socketd.transport.client.ClientConnectHandler import ClientConnectHandler, ClientConnectHandlerDefault
from socketd.transport.client.ClientConnector import ClientConnector
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Session import Session
from socketd.transport.client.ClientHeartbeatHandler import ClientHeartbeatHandler
from socketd.transport.core.impl.ProcessorDefault import ProcessorDefault
from socketd.transport.client.ClientConfig import ClientConfig

from loguru import logger


class ClientBase(ClientInternal, ABC):

    def __init__(self, client_config: ClientConfig, assistant:ChannelAssistant):
        self._processor = ProcessorDefault()
        self._heartbeat_handler: ClientHeartbeatHandler = None
        self._connect_handler: ClientConnectHandler = ClientConnectHandlerDefault
        self._config: ClientConfig = client_config
        self._assistant: ChannelAssistant = assistant

    def get_assistant(self):
        return self._assistant

    def get_config(self) -> ClientConfig:
        return self._config

    def get_processor(self):
        return self._processor

    def get_connectHandler(self) -> ClientConnectHandler:
        return self._connect_handler

    def get_heartbeatInterval(self) -> int:
        return self._config.get_heartbeat_interval()

    def get_heartbeatHandler(self) -> ClientHeartbeatHandler:
        return self._heartbeat_handler

    def connectHandler(self, connectHandler: ClientConnectHandler) -> 'Client':
        if connectHandler is not None:
            self._connect_handler = connectHandler
        return self

    def heartbeatHandler(self, heartbeatHandler: ClientHeartbeatHandler):
        if heartbeatHandler is not None:
            self._heartbeat_handler = heartbeatHandler
        return self


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

    async def _open_do(self, isThrow):
        connector: ClientConnector = self.create_connector()
        clientChannel: ClientChannel = ClientChannel(self, connector)
        try:
            await clientChannel.connect()
        except Exception as e:
            if isThrow:
                await clientChannel.close(code=Constants.CLOSE2008_OPEN_FAIL)
                raise SocketDException(f"Socket.D client Connection failed {e}")
        else:
            logger.info(f"Socket.D client successfully connected: {self._config.get_link_url()}")
        return clientChannel.get_session()

    def open(self) -> Awaitable[Session]:
        return self._open_do(False)

    def openOrThrow(self) -> Awaitable[Session]:
        return self._open_do(True)

    @abstractmethod
    def create_connector(self):
        ...
