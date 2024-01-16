import asyncio
from threading import Thread
from typing import Optional, Union

from websockets import ConnectionClosedError
from websockets.server import WebSocketServer, WebSocketServerProtocol

from socketd.transport.core.Channel import Channel
from socketd.transport.core.internal.ChannelDefault import ChannelDefault
from socketd.transport.core.Costants import Flag
from socketd.transport.core.Frame import Frame
from loguru import logger

from socketd_websocket.IWebSocketServer import IWebSocketServer

log = logger.opt()


class AIOWebSocketServerImpl(WebSocketServerProtocol, IWebSocketServer):

    def __init__(self, ws_handler, ws_server: WebSocketServer, ws_aio_server: 'WsAioServer',
                 *args, **kwargs):
        self._loop: asyncio.AbstractEventLoop = ws_aio_server.get_loop()
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

    async def on_error(self, conn: Union['AIOWebSocketServerImpl', WebSocketServerProtocol], ex: Exception):
        try:
            channel: Channel = conn.get_attachment()
            if channel is not None:
                # 有可能未 onOpen，就 onError 了；此时通道未成
                self.ws_aio_server.get_processor().on_error(channel, ex)
        except Exception as e:
            log.error(e)
            raise e

    async def on_message(self, conn: Union['AIOWebSocketServerImpl', WebSocketServerProtocol], path: str):
        """ws_handler"""
        while True:
            if conn.closed:
                break
            try:
                message = await self.recv()
                frame: Frame = self.ws_aio_server.get_assistant().read(
                    message)
                # # 采用线程池执行IO耗时任务
                # frame: Frame = await self.__loop.run_in_executor(self.ws_aio_server.get_config(
                # ).get_executor(), lambda _message: self.ws_aio_server.get_assistant().read( _message), message)
                if frame is not None:
                    # if conn.get_attachment().get_config().get_is_thread():
                    # asyncio.run_coroutine_threadsafe(
                    #     self.ws_aio_server.get_processor().on_receive(self.get_attachment(), frame),
                    #     self._loop)
                    # else:
                    await self.ws_aio_server.get_processor().on_receive(self.get_attachment(), frame)
                    if frame.get_flag() == Flag.Close:
                        """客户端主动关闭"""
                        await self.on_close(conn)
                        log.debug("{sessionId} 主动退出",
                                  sessionId=conn.get_attachment().get_session().get_session_id())
                        break
            except asyncio.CancelledError as c:
                logger.warning(c)
            except ConnectionClosedError as e:
                # 客户端异常关闭
                log.error(e)
                break
            except Exception as e:
                log.error(e)
                await self.on_error(conn, e)

    async def on_close(self, conn: Union['AIOWebSocketServerImpl', WebSocketServerProtocol]):
        """关闭tcp,结束握手"""
        await conn.close()
