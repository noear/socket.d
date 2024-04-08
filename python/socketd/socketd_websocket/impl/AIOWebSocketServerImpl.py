from __future__ import annotations
import asyncio
from typing import Optional, Union

from websockets import ConnectionClosedError, ConnectionClosedOK
from websockets.server import WebSocketServer, WebSocketServerProtocol

from socketd.transport.core.Channel import Channel
from socketd.transport.core.impl.LogConfig import log
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Frame import Frame


class AIOWebSocketServerImpl(WebSocketServerProtocol):

    def __init__(self, ws_handler, ws_server: WebSocketServer, ws_aio_server: 'WsAioServer',
                 *args, **kwargs):
        self.ws_aio_server = ws_aio_server
        self.__ws_server: WebSocketServer = ws_server
        self.__attachment: Optional[Channel] = None
        WebSocketServerProtocol.__init__(self=self, ws_handler=self.on_message, ws_server=self.__ws_server,
                                         *args,
                                         **kwargs)

    def set_attachment(self, obj: Channel):
        self.__attachment = obj

    def get_attachment(self) -> Channel:
        return self.__attachment

    def connection_open(self) -> None:
        """握手完成回调"""
        super().connection_open()
        log.debug("AIOWebSocketServerImpl connection_open")
        self.on_open(self)

    def handshake_handler(self):
        """socket_handshake"""
        log.debug("handshake_handler")

    def on_open(self, conn) -> None:
        """create_protocol"""
        if self.get_attachment() is None:
            channel = ChannelDefault(conn, self.ws_aio_server)
            self.set_attachment(channel)

    async def on_error(self, conn: Union[AIOWebSocketServerImpl, WebSocketServerProtocol], ex: Exception):
        try:
            channel: Channel = conn.get_attachment()
            if channel is not None:
                # 有可能未 onOpen，就 onError 了；此时通道未成
                self.ws_aio_server.get_processor().on_error(channel, ex)
        except Exception as e:
            log.error(e)
            raise e

    async def on_message(self, conn: Union[AIOWebSocketServerImpl, WebSocketServerProtocol], path: str):
        """ws_handler"""
        loop = asyncio.get_running_loop()
        tasks: list[asyncio.Task] = []
        while True:
            if conn.closed:
                break
            try:
                if tasks:
                    task = tasks[0]
                    if task.done():
                        tasks.pop(0)
                message = await self.recv()
                # frame: Frame = self.ws_aio_server.get_assistant().read(
                #     message)
                # # 采用线程池执行IO耗时任务
                frame: Frame = await loop.run_in_executor(self.ws_aio_server.get_config().get_exchange_executor(),
                                                          lambda _message: self.ws_aio_server.get_assistant().read(
                                                              _message), message)
                if frame is not None:
                    # 不等待直接运行
                    tasks.append(
                        loop.create_task(self.ws_aio_server.get_processor().on_receive(self.get_attachment(), frame)))
                    if frame.flag() == Flags.Close:
                        """客户端主动关闭"""
                        await self.on_close(conn)
                        log.debug("{sessionId} 主动退出",
                                  sessionId=conn.get_attachment().get_session().session_id())
                        break
            except asyncio.CancelledError as c:
                log.warning(c)
                break
            except ConnectionClosedOK as e:
                # received 1000 (OK); then sent 1000 (OK) 或者 1001  成功直接忽略
                log.info(e)
                break
            except ConnectionClosedError as e:
                # 客户端异常关闭
                log.error(e)
                break
            except Exception as e:
                log.error(e)
                await self.on_error(conn, e)
        try:
            # 等待未完成任务
            await asyncio.wait(tasks, timeout=10)
        except asyncio.CancelledError as c:
            pass
        except TimeoutError as e:
            log.warning(f"server on_receive timeout {e}")

    async def on_close(self, conn: Union[AIOWebSocketServerImpl, WebSocketServerProtocol]):
        """关闭tcp,结束握手"""
        await conn.close()
