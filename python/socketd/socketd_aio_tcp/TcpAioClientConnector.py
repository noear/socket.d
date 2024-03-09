import asyncio
import socket
from typing import Optional

from socketd.exception.SocketDExecption import SocketDTimeoutException, SocketDConnectionException
from socketd.transport.client.Client import ClientInternal
from socketd.transport.client.ClientConnectorBase import ClientConnectorBase
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.transport.core.Costants import Flag
from socketd.transport.core.Frame import Frame
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd.transport.utils.CompletableFuture import CompletableFuture

from socketd.transport.core.config.logConfig import log


class TcpAioClientConnector(ClientConnectorBase):

    def __init__(self, client: ClientInternal):
        super().__init__(client)
        self.__top: Optional[asyncio.Future] = None
        self.__real: Optional[socket.socket] = None
        self.__loop = asyncio.new_event_loop()

    async def connect(self):
        # 处理自定义架构的影响
        tcp_url = self.client.get_config().get_url().replace("std:", "").replace("-python", "")
        _sch, _host, _port = tcp_url.replace("//", "").split(":")
        if self.__top is None:
            self.__top = AsyncUtil.run_forever(self.__loop)
        _port = _port.split("/")[0]
        try:
            self.__real: socket.socket = socket.create_connection((_host, _port),
                                                                  timeout=self.client.get_config().get_idle_timeout())
            channel = ChannelDefault(self.__real, self.client)

            handshakeFuture = CompletableFuture()
            tasks = [
                asyncio.create_task(self.receive(channel, self.__real, handshakeFuture)),
                asyncio.create_task(channel.send_connect(tcp_url, self.client.get_config().get_meta_map()))
            ]
            # asyncio.run_coroutine_threadsafe(self.receive(channel, self.__real, handshakeFuture), self.__loop)
            # asyncio.run_coroutine_threadsafe(channel.send_connect(tcp_url, self.client.get_config().get_meta_map()), self.__loop)
            await asyncio.gather(*tasks)
            handshakeResult: ClientHandshakeResult = await handshakeFuture.get(
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

    async def receive(self, channel: ChannelDefault, _socket: socket.socket,
                      handshake_future: CompletableFuture) -> None:
        loop = asyncio.get_running_loop()
        while True:
            try:
                if getattr(_socket, '_closed'):
                    break
                task = loop.create_task(self.client.get_assistant().read(_socket))
                while True:
                    await asyncio.sleep(0)
                    if task.done():
                        frame: Frame = await task
                        if frame is not None:
                            if frame.get_flag() == Flag.Connack:
                                def future():
                                    b, _e = yield
                                    handshake_future.accept(ClientHandshakeResult(channel, e))

                                await channel.on_open_future(future)
                            await self.client.get_processor().on_message(channel, frame)
                    break
            except SocketDConnectionException as e:
                handshake_future.accept(ClientHandshakeResult(channel, e))
                break
            except Exception as e:
                self.client.get_processor().on_error(channel, e)
                break

    async def close(self):
        if self.__real is None:
            return
        try:
            self.__real.close()
            await self.stop()
        except Exception as e:
            log.debug(e)

    async def stop(self):
        if self.__top:
            _top = self.__top
            if not _top.done():
                _top.set_result(1)
        self.__loop.stop()
        log.debug(f"Stopping WebSocket::{self.__loop.is_running()}")
