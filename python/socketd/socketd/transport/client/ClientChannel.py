import asyncio
import traceback
from asyncio import Future

from socketd.exception.SocketDExecption import SocketDException, SocketDChannelException
from socketd.transport.client.Client import ClientInternal
from socketd.transport.core.Asserts import Asserts
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Costants import Constants
from socketd.utils.LogConfig import log
from socketd.transport.core.impl.SessionDefault import SessionDefault
from socketd.transport.stream.Stream import StreamInternal
from socketd.transport.core.impl.ChannelBase import ChannelBase
from socketd.transport.client.ClientConnector import ClientConnector

from socketd.transport.client.ClientHeartbeatHandler import ClientHeartbeatHandlerDefault
from socketd.utils.sync_api.AtomicRefer import AtomicRefer


class ClientChannel(ChannelBase):
    def __init__(self, client: ClientInternal, connector: ClientConnector):
        super().__init__(connector.get_config())
        self.__client = client
        self.__connector: ClientConnector = connector
        self.__sessionShell = SessionDefault(self)

        self.__real: ChannelInternal | None = None
        self.__heartbeatScheduledFuture: Future | None = None

        if client.get_heartbeat_handler():
            self.__heartbeatHandler: type[ClientHeartbeatHandlerDefault] = client.get_heartbeat_handler()
        else:
            self.__heartbeatHandler = ClientHeartbeatHandlerDefault

        self.__isConnecting = AtomicRefer(False)

        self.init_heartbeat()

    def __del__(self):
        try:
            if not self.__heartbeatScheduledFuture.done():
                self.__heartbeatScheduledFuture.cancel()
        except Exception as e:
            e_msg = traceback.format_exc()
            log.warning(e_msg)

    async def __heartbeatScheduled(self) -> None:
        while True:
            await asyncio.sleep(self.__client.get_heartbeat_interval() / 1000)
            await self.heartbeat_handle()

    def init_heartbeat(self):
        if self.__heartbeatScheduledFuture:
            self.__heartbeatScheduledFuture.cancel()

        if self.__connector.auto_reconnect():
            self.__heartbeatScheduledFuture = asyncio.create_task(self.__heartbeatScheduled())

    async def heartbeat_handle(self):
        if self.__real:
            if self.__real.get_handshake() is None:
                return

            if Asserts.is_closed_and_end(self.__real):
                log.debug("Client channel is closed (pause heartbeat), sessionId=" + self.get_session().session_id())
                await self.close(self.__real.close_code())
                return

            if self.__real.is_closing():
                return

        with self:
            try:
                await self.internalCheck()
                await self.__heartbeatHandler(self.get_session())
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

    def close_code(self):
        if self.__real is None:
            return False
        else:
            return self.__real.close_code()

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

    async def send(self, frame, stream: StreamInternal):
        Asserts.assert_closed_and_end(self.__real)

        try:
            await self.internalCheck()

            if self.__real is None:
                # 销毁心跳任务
                self.__heartbeatScheduledFuture.cancel()
                raise SocketDChannelException("Client channel is not connected")

            await self.__real.send(frame, stream)
        except SocketDException as s:
            raise s
        except Exception as e:
            if self.__connector.auto_reconnect():
                await self.internalCloseIfError()

            raise SocketDChannelException(f"Client channel send failed {e}")

    async def retrieve(self, frame, stream: StreamInternal):
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
            log.error(e)
        finally:
            await super().close(code)

    def get_session(self):
        return self.__sessionShell

    def on_error(self, error: Exception):
        self.__real.on_error(error)

    async def reconnect(self):
        self.init_heartbeat()
        await self.internalCheck()

    @log.catch
    async def connect(self):
        with self.__isConnecting as isConnected:
            if isConnected:
                return
            else:
                self.__isConnecting.set(True)

        try:
            if self.__real:
                await self.__real.close(Constants.CLOSE2002_RECONNECT)

            self.__real = await self.__client.get_connect_handler()(self.__connector)
            self.__real.set_session(self.__sessionShell)
            self.set_handshake(self.__real.get_handshake())
        except TimeoutError as t:
            log.error(f"socketD connect timed out: {t}")
        except Exception as e:
            log.error(e)
            raise SocketDChannelException(f"socketD connect")
        finally:
            self.__isConnecting.set(False)

    async def internalCloseIfError(self):
        if self.__real:
            await self.__real.close(Constants.CLOSE2001_ERROR)
            self.__real = None

    async def internalCheck(self):
        if self.__real is None or self.__real.is_valid() == False:
            await self.connect()
            return True
        else:
            return False

    def is_closing(self) -> bool:
        return self.__real.is_closing() if self.__real else 0

    def get_live_time(self) -> int:
        return self.__real.get_live_time() if self.__real else 0
