import asyncio
from concurrent.futures import Future
import socket
from typing import Optional, List

from socketd.exception.SocketDExecption import SocketDTimeoutException
from socketd.transport.core.ChannelSupporter import ChannelSupporter
from socketd.transport.core.Costants import Flag
from socketd.transport.core.Frame import Frame
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from socketd.transport.server.ServerBase import ServerBase
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.utils.AsyncUtil import AsyncUtil

from socketd.transport.core.config.logConfig import logger, log
from socketd.transport.utils.async_api.AtomicRefer import AtomicRefer

from .TcpAIOChannelAssistant import TcpAIOChannelAssistant


class TCPAIOServer(ServerBase, ChannelSupporter):

    def __init__(self, config: ServerConfig):
        self.__loop = asyncio.new_event_loop()
        super().__init__(config, TcpAIOChannelAssistant(config, self.__loop))
        self._server: Optional[socket.socket] = None
        self.__top: Optional[asyncio.Future] = None
        self._is_close: AtomicRefer = AtomicRefer(False)
        self._sock_future_list: List[asyncio.Future] = []
        self._server_forever_future: Optional[asyncio.Future | Future] = None
        self._sock_list: List[socket.socket] = []

    # 服务器的回调函数
    async def handler(self, loop: asyncio.AbstractEventLoop, sock: socket.socket,
                      addr, channel: ChannelDefault):  # reader和writer参数是asyncio.start_server生成异步服务器后自动传入进来的
        while True:  # 循环接受数据，直到套接字关闭
            try:
                if await self._is_close.get():
                    self.get_processor().on_close(channel)
                    break
                frame: Frame = await loop.create_task(self.get_assistant().read(sock))
                if frame is not None:
                    await self.get_processor().on_receive(channel, frame)
                    if frame.get_flag() == Flag.Close:
                        """客户端主动关闭"""
                        log.debug("{sessionId} 主动退出",
                                  sessionId=channel.get_session().get_session_id())
                        break
            except SocketDTimeoutException as e:
                await channel.send_close()
                log.error("server handler {e}", e=e)
                break
            except Exception as e:
                self.get_processor().on_error(channel, e)
                self.get_processor().on_close(channel)
                log.error("server handler {e}", e=e)
                break
        sock.close()

    async def server_forever(self, loop: asyncio.AbstractEventLoop, listener: socket.socket):
        while True:
            try:
                if await self._is_close.get():
                    break
                sock, addr = await loop.sock_accept(listener)
                channel = ChannelDefault(sock, self)
                self._sock_future_list.append(loop.create_task(self.handler(loop, sock, addr, channel)))
                self._sock_list.append(sock)
            except asyncio.CancelledError as e:
                log.warning("Server asyncio cancelled {e}", e=e)
                break
            except Exception as e:
                log.warning("Server accept error {e}", e=e)
                break
        listener.close()

    async def start(self):
        # 生成一个服务器
        self._server: socket.socket = socket.create_server((self.get_config().get_host(),
                                                            self.get_config().get_port()))
        self._server.setblocking(False)
        if self.__top is None or not self.__loop.is_running():
            self.__top = AsyncUtil.run_forever(self.__loop)
        self._server_forever_future = asyncio.run_coroutine_threadsafe(self.server_forever(self.__loop, self._server), self.__loop)
        return self._server

    async def close_wait(self):
        # asyncio.run_coroutine_threadsafe(asyncio.wait(self._sock_future_list), self.__loop).result()
        await asyncio.wait(self._sock_future_list, timeout=10)
        for sock in self._sock_list:
            sock.detach()
            sock.close()
        try:
            if self._server_forever_future:
                self._server_forever_future.result(10)
        except asyncio.TimeoutError:
            self._server_forever_future.cancel()
            log.debug("Server server_forever timeout")

    async def stop(self):
        log.info("TcpAioServer stop...")
        # 等等执行完成
        await self._is_close.set(True)
        await self.close_wait()
        if not self.__top.done():
            self.__top.set_result(True)
        self._server.close()
        self.__top = None
