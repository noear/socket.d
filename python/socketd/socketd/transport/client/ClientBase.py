from abc import ABC, abstractmethod
from typing import Optional

from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.client.Client import ClientInternal, Client
from socketd.transport.client.ClientChannel import ClientChannel
from socketd.transport.client.ClientConfigHandler import ClientConfigHandler
from socketd.transport.client.ClientConnectHandler import ClientConnectHandler, ClientConnectHandlerDefault
from socketd.transport.client.ClientConnector import ClientConnector
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Session import Session
from socketd.transport.client.ClientHeartbeatHandler import ClientHeartbeatHandler
from socketd.utils.LogConfig import log
from socketd.transport.core.impl.ProcessorDefault import ProcessorDefault
from socketd.transport.client.ClientConfig import ClientConfig



class ClientBase(ClientInternal, ABC):

    def __init__(self, client_config: ClientConfig, assistant: ChannelAssistant):
        self.__config: ClientConfig = client_config
        self.__assistant: ChannelAssistant = assistant

        self.__processor = ProcessorDefault()
        self.__heartbeat_handler: Optional[ClientHeartbeatHandler] = None
        self.__connect_handler: type[ClientConnectHandler] = ClientConnectHandlerDefault

    def get_assistant(self):
        return self.__assistant

    def get_config(self) -> ClientConfig:
        return self.__config

    def get_processor(self):
        return self.__processor

    def get_connect_handler(self) -> ClientConnectHandler:
        return self.__connect_handler

    def get_heartbeat_interval(self) -> int:
        return self.__config.get_heartbeat_interval()

    def get_heartbeat_handler(self) -> ClientHeartbeatHandler:
        return self.__heartbeat_handler

    def connect_handler(self, connectHandler: ClientConnectHandler) -> Client:
        if connectHandler:
            self.__connect_handler = connectHandler
        return self

    def heartbeat_handler(self, heartbeatHandler: ClientHeartbeatHandler):
        if heartbeatHandler:
            self.__heartbeat_handler = heartbeatHandler
        return self

    def config(self, configHandler: ClientConfigHandler):
        if configHandler:
            configHandler(self.__config)
        return self

    def process(self, processor):
        if processor:
            self.__processor = processor
        return self

    def listen(self, listener):
        if listener:
            self.__processor.set_listener(listener)
        return self

    async def _open_do(self, isThrow):
        connector: ClientConnector = self.create_connector()
        clientChannel: ClientChannel = ClientChannel(self, connector)

        try:
            await clientChannel.connect()
            log.info(f"Socket.D client successfully connected: link={self.get_config().get_link_url()}")
        except Exception as e:
            if isThrow:
                await clientChannel.close(code=Constants.CLOSE2008_OPEN_FAIL)
                raise SocketDException(f"Socket.D client Connection failed {e}")
            else:
                log.info(f"Socket.D client Connection failed: link={self.get_config().get_link_url()}")

        return clientChannel.get_session()

    async def open(self) -> Session:
        return await self._open_do(False)

    async def open_or_throw(self) -> Session:
        return await self._open_do(True)

    @abstractmethod
    def create_connector(self):
        ...
