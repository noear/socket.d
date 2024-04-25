import asyncio
import socket
from asyncio import StreamReader, StreamReaderProtocol, StreamWriter
from typing import Optional, AsyncGenerator, List

from socketd.exception.SocketDExecption import SocketDTimeoutException, SocketDConnectionException
from socketd.transport.client.Client import ClientInternal
from socketd.transport.client.ClientConnectorBase import ClientConnectorBase
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Frame import Frame
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from socketd.utils.LogConfig import log
from socketd.utils.AsyncUtils import AsyncUtils
from socketd.utils.CompletableFuture import CompletableFuture

from socketd_aio_tcp.TCPStreamIO import TCPStreamIO


class TcpAioClientConnector(ClientConnectorBase):

    def __init__(self, client: ClientInternal):
        super().__init__(client)
        self._sock: Optional[socket.socket] = None
        self.__top: Optional[asyncio.Future] = None
        self.__real: Optional[asyncio.Transport] = None
        self.__message: asyncio.Queue = asyncio.Queue()
        self.__message_future: Optional[asyncio.Future] = None
        self._handshakeFuture = CompletableFuture()
        self._handshakeTask: Optional[AsyncGenerator] = None
        self._receiveTask: Optional[asyncio.Task] = None
        self.transfer_dataTask: Optional[asyncio.Task] = None
        self._on_receive_tasks: List[asyncio.Task] = []
        self._loop: Optional[asyncio.AbstractEventLoop] = None

    async def connect(self):
        # 处理自定义架构的影响
        loop = asyncio.get_running_loop()
        tcp_url = self.client.get_config().get_url().replace("-python", "")
        _sch, _host, _port = tcp_url.replace("//", "").split(":")
        _port = int(_port.split("/")[0])
        if self.__top is None:
            self._loop = asyncio.new_event_loop()
            self.__top = AsyncUtils.run_forever(self._loop, daemon=True)
        try:
            
            reader = StreamReader(limit=self.get_config().get_read_buffer_size(), loop=loop)
            protocol = StreamReaderProtocol(reader, loop=loop)
            self._sock: socket.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self._sock.connect((_host, _port))
            transport, _ = await loop.create_connection(
                lambda: protocol, sock=self._sock)
            writer = StreamWriter(transport, protocol, reader, loop)

            self.__real: asyncio.Transport = transport

            channel = ChannelDefault(TCPStreamIO(self._sock, reader, writer), self.client)
            self._receiveTask = loop.create_task(self.receive(channel, self._handshakeFuture))
            self.transfer_dataTask = loop.create_task(self.transfer_data(reader))

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

    async def transfer_data(self, _reader: asyncio.StreamReader) -> None:
        while True:
            try:
                if await self.is_closed():
                    break
                _frame: Frame = await self.client.get_assistant().read(_reader)
                if _frame is not None:
                    await self.__message.put(_frame)
                    if _frame.flag() == Flags.Close:
                        break
            except asyncio.CancelledError as e:
                break

    async def receive(self, channel: ChannelDefault,
                      handshake_future: CompletableFuture) -> None:
        loop = asyncio.get_running_loop()
        while True:
            try:
                if self._on_receive_tasks:
                    task = self._on_receive_tasks[0]
                    if task.done():
                        self._on_receive_tasks.pop(0)
                self.__message_future = loop.create_task(self.__message.get())
                if await self.is_closed():
                    break
                frame: Frame = await self.__message_future
                self.__message.task_done()
                if frame is not None:
                    if frame.flag() == Flags.Connack:
                        async def future(b, _e):
                            handshake_future.accept(ClientHandshakeResult(channel, _e))
                        await channel.on_open_future(future)
                    self._on_receive_tasks.append(loop.create_task(self.client.get_processor().on_receive(channel, frame)))
                    if frame.flag() == Flags.Close:
                        break
            except asyncio.TimeoutError as e:
                break
            except asyncio.CancelledError as e:
                break
            except SocketDConnectionException as e:
                handshake_future.accept(ClientHandshakeResult(channel, e))
                break
            except Exception as e:
                self.client.get_processor().on_error(channel, e)
                break

    async def is_closed(self):
        return self.__real.is_closing()

    async def close(self):
        log.debug("TcpAioClientConnector stop... ")
        if self.__real is None:
            return
        try:
            self.__real.close()
            if self._sock is not None and not getattr(self._sock, "_closed"):
                self._sock.close()
            await self.stop()
        except Exception as e:
            log.debug(e)

    async def stop(self):
        self.__message_future.cancel()
        self.transfer_dataTask.cancel()
        try:
            await asyncio.wait([self._receiveTask], timeout=5)
        except asyncio.CancelledError:
            log.debug("_receiveTask Cancelling")
        if self.__top:
            self.__top.set_result(None)
        self._loop.stop()
        self.__top = None
