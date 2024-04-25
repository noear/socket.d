import asyncio
import socket
from asyncio import StreamReader, StreamReaderProtocol, StreamWriter
from typing import Optional, List

from socketd.exception.SocketDExecption import SocketDTimeoutException
from socketd.transport.core.ChannelSupporter import ChannelSupporter
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Frame import Frame
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from socketd.utils.LogConfig import log
from socketd.transport.server.ServerBase import ServerBase
from socketd.transport.server.ServerConfig import ServerConfig

from socketd.utils.async_api.AtomicRefer import AtomicRefer
from .TCPStreamIO import TCPStreamIO

from .TcpAIOChannelAssistant import TcpAIOChannelAssistant


class TCPAIOServer(ServerBase, ChannelSupporter):

    def __init__(self, config: ServerConfig):
        self._loop: asyncio.AbstractEventLoop = asyncio.get_running_loop()
        super().__init__(config, TcpAIOChannelAssistant(config, self._loop))
        self._server: Optional[asyncio.Server] = None
        self._sock: Optional[socket.socket] = None
        self._is_close: AtomicRefer = AtomicRefer(False)
        self._on_receive_tasks: List[asyncio.Task] = []

    # 服务器的回调函数
    async def server_forever(self, reader: StreamReader, writer: StreamWriter):

        channel = ChannelDefault(TCPStreamIO(self._sock, reader, writer), self)
        while True:
            try:
                if self._on_receive_tasks:
                    task = self._on_receive_tasks[0]
                    if task.done():
                        self._on_receive_tasks.pop(0)
                if await self._is_close.get():
                    await self.get_processor().on_close(channel)
                    break
                frame: Frame = await self.get_assistant().read(reader)
                if frame is not None:
                    self._on_receive_tasks.append(self._loop.create_task(self.get_processor().on_receive(channel, frame)))
                    if frame.flag() == Flags.Close:
                        """客户端主动关闭"""
                        log.debug("{sessionId} 主动退出",
                                  sessionId=channel.get_session().session_id())
                        break
            except SocketDTimeoutException as e:
                await channel.send_close(Constants.CLOSE1001_PROTOCOL_CLOSE)
                log.error("server handler {e}", e=e)
                break
            except Exception as e:
                self.get_processor().on_error(channel, e)
                await self.get_processor().on_close(channel)
                log.error("server handler {e}", e=e)
                break

    async def start(self):
        loop = asyncio.get_running_loop()

        def factory():
            reader = StreamReader(limit=self.get_config().get_read_buffer_size(), loop=loop)
            protocol = StreamReaderProtocol(reader, self.server_forever,
                                            loop=loop)
            return protocol

        self._sock: socket.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._sock.bind(("127.0.0.1" if not self.get_config().get_host() else self.get_config().get_host(),
                         self.get_config().get_port()))
        self._sock.settimeout(self.get_config().get_idle_timeout() / 1000)
        # 生成一个服务器
        self._server: asyncio.Server = await loop.create_server(factory,
                                                                sock=self._sock,
                                                                start_serving=True,
                                                                )
        await self._server

    async def stop(self):
        log.info("TcpAioServer stop...")
        # 等等执行完成
        await self._is_close.set(True)
        self._server.close()
        if self._sock is not None and not getattr(self._sock, "_closed"):
            self._sock.close()
        # await self._server.wait_closed()


