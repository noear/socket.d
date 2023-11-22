from abc import ABC

from socketd.core.AssertsUtil import AssertsUtil
from socketd.core.Channel import Channel
from socketd.core.ChannelBase import ChannelBase
from socketd.transport.client.ClientConnector import ClientConnector
from loguru import logger


class ClientChannel(ChannelBase, ABC):
    def __init__(self, real: Channel, connector: ClientConnector):
        super().__init__(real.get_config())
        self.real = real
        self.connector: ClientConnector = connector
        self.heartbeatHandler = connector.heartbeatHandler()

        # if self.heartbeatHandler is None:
        #     self.heartbeatHandler = HeartbeatHandlerDefault()
        connector.autoReconnect()
        # if connector.autoReconnect() and self.heartbeatScheduledFuture is None:
        #     self.heartbeatScheduledFuture = threading.Timer(connector.heartbeatInterval(), self.heartbeatHandle)
        #     self.heartbeatScheduledFuture.start()

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

    def heartbeat_handle(self):
        AssertsUtil.assert_closed(self.real)

        with self:
            try:
                self.prepare_send()
                self.heartbeatHandler.heartbeat_handle()
            except Exception as e:
                if self.connector.autoReconnect():
                    self.real.close()
                    self.real = None
                raise e

    async def send(self, frame, acceptor):
        AssertsUtil.assert_closed(self.real)
        try:
            self.prepare_send()
            await self.real.send(frame, acceptor)
        except Exception as e:
            if self.connector.autoReconnect():
                await self.real.close()
                self.real = None
            raise e

    def retrieve(self, frame):
        self.real.retrieve(frame)

    def getSession(self):
        return self.real.get_session()

    async def close(self, code: int = 1000,
                    reason: str = "", ):
        try:
            await super().close(code, reason)
            if self.real is not None:
                await self.real.close()
        except Exception as e:
            logger.error(e)

    def prepare_send(self):
        if self.real is None or not self.real.is_valid():
            self.real = self.connector.connect()
            return True
        else:
            return False
