import asyncio
from abc import ABC
from asyncio import Future

from socketd.core.AssertsUtil import AssertsUtil
from socketd.core.Channel import Channel
from socketd.core.ChannelBase import ChannelBase
from socketd.transport.client.ClientConnector import ClientConnector
from loguru import logger

from socketd.transport.core.AsyncUtil import AsyncUtil
from socketd.transport.core.HeartbeatHandlerDefault import HeartbeatHandlerDefault


class ClientChannel(ChannelBase, ABC):
    def __init__(self, real: Channel, connector: ClientConnector):
        super().__init__(real.get_config())
        self.real: Channel = real
        self.connector: ClientConnector = connector
        self.heartbeatHandler = connector.heartbeatHandler()
        self._heartbeatScheduledFuture: Future | None = None

        if self.heartbeatHandler is None:
            self.heartbeatHandler = HeartbeatHandlerDefault()

        self._loop = asyncio.new_event_loop()
        self.initHeartbeat()

    def __del__(self):
        if self._loop:
            self._loop.close()

    def remove_acceptor(self, sid):
        if self.real is not None:
            self.real.remove_acceptor(sid)

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
                    await asyncio.sleep(self.connector.heartbeatInterval())
                    await self.heartbeat_handle()
            self._heartbeatScheduledFuture = asyncio.create_task(_heartbeatScheduled())

            self.get_config().get_executor().submit(lambda:
                                                    AsyncUtil.thread_handler(self._loop, self._heartbeatScheduledFuture))

    def heartbeat_handle(self):
        AssertsUtil.assert_closed(self.real)

        with self:
            try:
                self.prepare_send()
                self.heartbeatHandler.heartbeat(self.get_session())
            except Exception as e:
                if self.connector.autoReconnect():
                    self.real.close()
                    self.real = None
                raise e

    async def send(self, frame, acceptor):
        AssertsUtil.assert_closed(self.real)
        with self:
            try:
                await self.prepare_send()
                await self.real.send(frame, acceptor)
            except Exception as e:
                if self.connector.autoReconnect():
                    await self.real.close()
                    self.real = None
                raise e

    async def retrieve(self, frame, on_error):
        await self.real.retrieve(frame, on_error)

    def get_session(self):
        return self.real.get_session()

    async def close(self, code: int = 1000,
                    reason: str = "", ):
        try:
            await super().close(code, reason)
            self._heartbeatScheduledFuture.cancel()
            if self.real is not None:
                await self.real.close()
        except Exception as e:
            logger.error(e)

    async def prepare_send(self):
        if self.real is None or not self.real.is_valid():
            self.real = await self.connector.connect()
            return True
        else:
            return False

    def get_real(self):
        return self.real

    def on_error(self, error: Exception):
        pass
