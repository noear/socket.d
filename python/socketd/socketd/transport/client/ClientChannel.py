import asyncio
from abc import ABC
from asyncio import Future

from socketd.exception.SocketDExecption import SocketDException, SocketDChannelException
from socketd.transport.client.Client import ClientInternal
from socketd.transport.core.Asserts import Asserts
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Costants import Constants
from socketd.transport.core.impl.SessionDefault import SessionDefault
from socketd.transport.stream.StreamManger import StreamInternal
from socketd.transport.core.impl.ChannelBase import ChannelBase
from socketd.transport.client.ClientConnector import ClientConnector
from loguru import logger

from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd.transport.client.ClientHeartbeatHandler import ClientHeartbeatHandlerDefault
from socketd.transport.utils.sync_api.AtomicRefer import AtomicRefer


class ClientChannel(ChannelBase, ABC):
    def __init__(self,  client:ClientInternal, connector: ClientConnector):
        super().__init__(connector.get_config())
        self.__client = client
        self.__connector: ClientConnector = connector
        self.__sessionShell = SessionDefault(self)

        self.__real: ChannelInternal|None = None
        self.__heartbeatScheduledFuture: Future | None = None

        if client.get_heartbeat_handler():
            self.__heartbeatHandler = client.get_heartbeat_handler()
        else:
            self.__heartbeatHandler = ClientHeartbeatHandlerDefault

        self.__loop = asyncio.new_event_loop()
        self.__isConnecting = AtomicRefer(False)

        self.init_heartbeat()

    def __del__(self):
        try:
            if self.__loop:
                self.__loop.stop()
        except Exception as e:
            logger.warning(e)

    def init_heartbeat(self):
        if self.__heartbeatScheduledFuture:
            self.__heartbeatScheduledFuture.cancel()

        if self.__connector.auto_reconnect():
            async def _heartbeatScheduled():
                while True:
                    await asyncio.sleep(self.__client.get_heartbeat_interval() / 100)
                    await self.heartbeat_handle()

            self.__heartbeatScheduledFuture = asyncio.create_task(_heartbeatScheduled())
            self.get_config().get_exchange_executor().submit(lambda:
                                                    AsyncUtil.thread_handler(self.__loop,
                                                                             self.__heartbeatScheduledFuture))

    async def heartbeat_handle(self):
        if self.__real:
            if self.__real.get_handshake() is None:
                return

            if Asserts.is_closed_and_end(self.__real):
                logger.debug("Client channel is closed (pause heartbeat), sessionId=" + self.get_session().session_id())
                self.close(self.__real.is_closed())
                return

            if self.__real.is_closing():
                return

        with self:
            try:
                await self.internalCheck()
                await self.__heartbeatHandler.clientHeartbeat(self.get_session())
            except Exception as e:
                if self.__connector.auto_reconnect():
                    if self.__real:
                        await self.__real.close(code=Constants.CLOSE2001_ERROR)
                    self.__real = None
                raise e


    def is_valid(self):
        if self.__real is None:
            return False
        else:
            return self.__real.is_valid()

    def is_closed(self):
        if self.__real is None:
            return False
        else:
            return self.__real.is_closed()

    def get_remote_address(self):
        if self.__real is None:
            return None
        else:
            return self.__real.get_remote_address()

    def get_local_address(self):
        if self.__real is None:
            return None
        else:
            return self.__real.get_local_address()

    async def send(self, frame, stream:StreamInternal):
        Asserts.assert_closed_and_end(self.__real)

        try:
            await self.internalCheck()

            if self.__real is None:
                raise SocketDChannelException("Client channel is not connected")

            await self.__real.send(frame, stream)
        except SocketDException as s:
            raise s
        except Exception as e:
            if self.__connector.auto_reconnect():
                self.internalCloseIfError()

            raise SocketDChannelException(f"Client channel send failed {e}")

    async def retrieve(self, frame, stream:StreamInternal):
        await self.__real.retrieve(frame, stream)

    async def close(self, code):
        try:
            if self.__heartbeatScheduledFuture:
                self.__heartbeatScheduledFuture.cancel()

            if self.__connector:
                await self.__connector.close()

            if self.__real:
                await self.__real.close(code)
        except Exception as e:
            logger.error(e)
        finally:
            await super().close(code)

    def get_session(self):
        return self.__sessionShell

    def on_error(self, error: Exception):
        self.__real.on_error(error)

    async def reconnect(self):
        self.init_heartbeat()
        await self.internalCheck()

    async def connect(self):
        with self.__isConnecting as isConnected:
            if isConnected:
                self.__isConnecting.set(True)
            else:
                return

        try:
            if self.__real:
                await self.__real.close(Constants.CLOSE2002_RECONNECT)

            self.__real = await self.__client.get_connect_handler()(self.__connector)
            self.__real.set_session(self.__sessionShell)
            self.set_handshake(self.__real.handshake())
        except TimeoutError as t:
            logger.error(f"socketD connect timed out: {t}")
        except Exception as e:
            logger.error(e)
        finally:
            self.__isConnecting.set(False)

    def internalCloseIfError(self):
        if self.__real:
            self.__real.close(Constants.CLOSE2001_ERROR)
            self.__real = None


    async def internalCheck(self):
        if self.__real is None or self.__real.is_valid() == False:
            self.connect()
            return True
        else:
            return False
