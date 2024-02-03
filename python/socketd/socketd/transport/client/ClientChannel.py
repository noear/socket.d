import asyncio
from typing import Optional
from abc import ABC
from asyncio import Future

from socketd.exception.SocketDExecption import SocketDException, SocketDChannelException
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Costants import Constants
from socketd.transport.core.impl.SessionDefault import SessionDefault
from socketd.transport.utils.AssertsUtil import AssertsUtil
from socketd.transport.core.impl.ChannelBase import ChannelBase
from socketd.transport.client.ClientConnector import ClientConnector
from loguru import logger

from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd.transport.core.impl.HeartbeatHandlerDefault import HeartbeatHandlerDefault
from socketd.transport.utils.sync_api.AtomicRefer import AtomicRefer


class ClientChannel(ChannelBase, ABC):
    def __init__(self,  connector: ClientConnector):
        super().__init__(connector.get_config())
        self.real: Optional[ChannelInternal] = None
        self._session = SessionDefault(self)
        self.connector: ClientConnector = connector
        self.heartbeatHandler = connector.heartbeatHandler()
        self._heartbeatScheduledFuture: Future | None = None

        if self.heartbeatHandler is None:
            self.heartbeatHandler = HeartbeatHandlerDefault()

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
        if self.real is None:
            return False
        else:
            return self.real.is_valid()

    def is_closed(self):
        if self.real is None:
            return False
        else:
            return self.real.is_closed()

    def get_remote_address(self):
        if self.real is None:
            return None
        else:
            return self.real.get_remote_address()

    def get_local_address(self):
        if self.real is None:
            return None
        else:
            return self.real.get_local_address()

    def initHeartbeat(self):
        if self._heartbeatScheduledFuture is not None:
            self._heartbeatScheduledFuture.cancel()

        if self.connector.autoReconnect():
            async def _heartbeatScheduled():
                while True:
                    await asyncio.sleep(self.connector.heartbeatInterval()/100)
                    await self.heartbeat_handle()

            self._heartbeatScheduledFuture = asyncio.create_task(_heartbeatScheduled())
            self.get_config().get_executor().submit(lambda:
                                                    AsyncUtil.thread_handler(self._loop,
                                                                             self._heartbeatScheduledFuture))

    async def heartbeat_handle(self):
        AssertsUtil.assert_closed(self.real)

        with self:
            try:
                await self.prepare_check()
                await self.heartbeatHandler.heartbeat(self.get_session())
            except Exception as e:
                if self.connector.autoReconnect():
                    if self.real:
                        await self.real.close(code=Constants.CLOSE21_ERROR)
                    self.real = None
                raise e

    async def send(self, frame, acceptor):
        AssertsUtil.assert_closed(self.real)
        try:
            await self.prepare_check()
            await self.real.send(frame, acceptor)
        except SocketDException as s:
            raise s
        except Exception as e:
            if self.connector.autoReconnect():
                if self.real:
                    await self.real.close(Constants.CLOSE21_ERROR)
                self.real = None
            raise SocketDChannelException(f"Client channel send failed {e}")

    async def retrieve(self, frame, on_error):
        await self.real.retrieve(frame, on_error)

    def get_session(self):
        return self._session

    async def close(self, code):
        try:
            await super().close(code)
            if self._heartbeatScheduledFuture:
                self._heartbeatScheduledFuture.cancel()
            await self.connector.close()
            if self.real is not None:
                await self.real.close(code)
        except Exception as e:
            logger.error(e)

    async def prepare_check(self):
        if self.real is None or not self.real.is_valid():
            self.real = await self.connector.connect()
            return True
        else:
            return False

    def get_real(self):
        return self.real

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
            if self.real:
                await self.real.close(Constants.CLOSE22_RECONNECT)
            self.real: ChannelInternal = await self.connector.connect()
            self.real.set_session(self._session)
            self.set_handshake(self.real.get_handshake())
        except TimeoutError as t:
            logger.error(f"socketD connect timed out: {t}")
        except Exception as e:
            logger.error(e)
        finally:
            self._isConnecting.set(False)
