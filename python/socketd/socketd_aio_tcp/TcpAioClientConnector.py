import asyncio
import socket
from typing import Optional, AsyncGenerator

from socketd.exception.SocketDExecption import SocketDTimeoutException, SocketDConnectionException
from socketd.transport.client.Client import ClientInternal
from socketd.transport.client.ClientConnectorBase import ClientConnectorBase
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Frame import Frame
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from socketd.transport.core.impl.LogConfig import log
from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd.transport.utils.CompletableFuture import CompletableFuture

from socketd.transport.utils.async_api.AtomicRefer import AtomicRefer


class TcpAioClientConnector(ClientConnectorBase):

    def __init__(self, client: ClientInternal):
        super().__init__(client)
        self.__top: Optional[asyncio.Future] = None
        self.__real: Optional[socket.socket] = None
        self.__loop = asyncio.new_event_loop()
        self.__message: asyncio.Queue = asyncio.Queue()
        self._handshakeFuture = CompletableFuture()
        self._handshakeTask: Optional[AsyncGenerator] = None
        self._receiveTask: Optional[asyncio.Task] = None
        self.transfer_dataTask: Optional[asyncio.Task] = None

    async def connect(self):
        # 处理自定义架构的影响
        tcp_url = self.client.get_config().get_url().replace("std:", "").replace("-python", "")
        _sch, _host, _port = tcp_url.replace("//", "").split(":")
        if self.__top is None:
            self.__top = AtomicRefer(AsyncUtil.run_forever(self.__loop))
        if not self.__loop.is_running():
            self.__top = AtomicRefer(AsyncUtil.run_forever(self.__loop))
        _port = _port.split("/")[0]
        try:
            self.__real: socket.socket = socket.create_connection((_host, _port),
                                                                  timeout=self.client.get_config().get_idle_timeout())
            channel = ChannelDefault(self.__real, self.client)

            self._receiveTask = asyncio.create_task(self.receive(channel, self.__real, self._handshakeFuture))
            self.transfer_dataTask = asyncio.create_task(self.transfer_data(self.__real))

            await channel.send_connect(tcp_url, self.client.get_config().get_meta_map())
            handshakeResult: ClientHandshakeResult = await self._handshakeFuture.get(
                self.client.get_config().get_connect_timeout())
            if _e := handshakeResult.get_throwable():
                raise _e
            else:
                return handshakeResult.get_channel()
        except TimeoutError as t:
            await self.close()
            raise SocketDTimeoutException(f"Connection timeout: {self.client.get_config().get_link_url()}")
        except IOError as o:
            await self.close()
            raise o
        except Exception as e:
            await self.close()
            raise SocketDTimeoutException(f"Connection timeout: {self.client.get_config().get_link_url()} {e}")

    async def transfer_data(self, _sock: socket.socket) -> None:

        while True:
            try:
                if await self.is_closed():
                    break
                _frame: Frame = await self.client.get_assistant().read(_sock)
                if _frame is None:
                    break
                await self.__message.put(_frame)
                if _frame.flag() == Flags.Close:
                    break
            except asyncio.CancelledError as e:
                break
            except ConnectionAbortedError as e:
                break
        await self.close()

    async def receive(self, channel: ChannelDefault, _socket: socket.socket,
                      handshake_future: CompletableFuture) -> None:
        while True:
            try:
                if await self.is_closed():
                    break
                frame: Frame = await self.__message.get()
                if frame is not None:
                    if frame.flag() == Flags.Connack:
                        async def future(b, _e):
                            handshake_future.accept(ClientHandshakeResult(channel, _e))

                        await channel.on_open_future(future)
                    await self.client.get_processor().on_receive(channel, frame)
                    if frame.flag() == Flags.Close:
                        break
            except SocketDConnectionException as e:
                handshake_future.accept(ClientHandshakeResult(channel, e))
                break
            except Exception as e:
                self.client.get_processor().on_error(channel, e)
                break
        await self.close()

    async def is_closed(self):
        return getattr(self.__real, '_closed')

    async def close(self):
        log.debug("TcpAioClientConnector stop... ")
        if self.__real is None:
            return
        try:
            self._receiveTask.cancel()
            self.transfer_dataTask.cancel()
            self.__real.close()
            await self.stop()
        except Exception as e:
            log.debug(e)

    async def stop(self):
        if self.__top:
            async with self.__top as _top:
                if not _top.done():
                    _top.cancel()
        if self.__loop.is_running():
            self.__loop.stop()
        log.debug(f"Stopping TCP::{self.__loop.is_running()}")
