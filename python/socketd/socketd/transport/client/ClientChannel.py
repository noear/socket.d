import asyncio
from typing import Optional
from abc import ABC
from asyncio import Future

from socketd.exception.SocketDExecption import SocketDException, SocketDChannelException
from socketd.transport.client.Client import ClientInternal
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Costants import Constants
from socketd.transport.core.impl.SessionDefault import SessionDefault
from socketd.transport.utils.AssertsUtil import AssertsUtil
from socketd.transport.core.impl.ChannelBase import ChannelBase
from socketd.transport.client.ClientConnector import ClientConnector
from loguru import logger

from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd.transport.client.ClientHeartbeatHandler import ClientHeartbeatHandlerDefault
from socketd.transport.utils.sync_api.AtomicRefer import AtomicRefer


class ClientChannel(ChannelBase, ABC):
    def __init__(self,  client:ClientInternal, connector: ClientConnector):
        super().__init__(connector.get_config())
        self._client = client
        self._real: Optional[ChannelInternal] = None
        self._session = SessionDefault(self)
        self._connector: ClientConnector = connector
        self._heartbeatHandler = connector.heartbeatHandler()
        self._heartbeatScheduledFuture: Future | None = None

        if self._heartbeatHandler is None:
            self._heartbeatHandler = ClientHeartbeatHandlerDefault

        self._loop = asyncio.new_event_loop()
        self.initHeartbeat()
        self._isConnecting = AtomicRefer(False)

    def __del__(self):
        try:
            if self._loop:
                self._loop.stop()
        except Exception as e:
            logger.warning(e)

    def is_valid(self):
        if self._real is None:
            return False
        else:
            return self._real.is_valid()

    def is_closed(self):
        if self._real is None:
            return False
        else:
            return self._real.is_closed()

    def get_remote_address(self):
        if self._real is None:
            return None
        else:
            return self._real.get_remote_address()

    def get_local_address(self):
        if self._real is None:
            return None
        else:
            return self._real.get_local_address()

    def initHeartbeat(self):
        if self._heartbeatScheduledFuture is not None:
            self._heartbeatScheduledFuture.cancel()

        if self._connector.autoReconnect():
            async def _heartbeatScheduled():
                while True:
                    await asyncio.sleep(self._connector.heartbeatInterval() / 100)
                    await self.heartbeat_handle()

            self._heartbeatScheduledFuture = asyncio.create_task(_heartbeatScheduled())
            self.get_config().get_executor().submit(lambda:
                                                    AsyncUtil.thread_handler(self._loop,
                                                                             self._heartbeatScheduledFuture))

    async def heartbeat_handle(self):
        AssertsUtil.assert_closed(self._real)

        with self:
            try:
                await self.prepare_check()
                await self._heartbeatHandler.clientHeartbeat(self.get_session())
            except Exception as e:
                if self._connector.autoReconnect():
                    if self._real:
                        await self._real.close(code=Constants.CLOSE21_ERROR)
                    self._real = None
                raise e

    async def send(self, frame, acceptor):
        AssertsUtil.assert_closed(self._real)
        try:
            await self.prepare_check()
            await self._real.send(frame, acceptor)
        except SocketDException as s:
            raise s
        except Exception as e:
            if self._connector.autoReconnect():
                if self._real:
                    await self._real.close(Constants.CLOSE21_ERROR)
                self._real = None
            raise SocketDChannelException(f"Client channel send failed {e}")

    async def retrieve(self, frame, on_error):
        await self._real.retrieve(frame, on_error)

    def get_session(self):
        return self._session

    async def close(self, code):
        try:
            await super().close(code)
            if self._heartbeatScheduledFuture:
                self._heartbeatScheduledFuture.cancel()
            await self._connector.close()
            if self._real is not None:
                await self._real.close(code)
        except Exception as e:
            logger.error(e)

    async def prepare_check(self):
        if self._real is None or not self._real.is_valid():
            self._real = await self._connector.connect()
            return True
        else:
            return False

    def get_real(self):
        return self._real

    def on_error(self, error: Exception):
        pass

    async def reconnect(self):
        self.initHeartbeat()
        await self.prepare_check()

    async def connect(self):
        with self._isConnecting as isConnected:
            if isConnected:
                self._isConnecting.set(True)
        try:
            if self._real:
                await self._real.close(Constants.CLOSE22_RECONNECT)

            self._real: ChannelInternal = await self._client.get_connectHandler().clientConnect(self._connector)
            self._real.set_session(self._session)
            self.set_handshake(self._real.get_handshake())
        except TimeoutError as t:
            logger.error(f"socketD connect timed out: {t}")
        except Exception as e:
            logger.error(e)
        finally:
            self._isConnecting.set(False)
