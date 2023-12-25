import asyncio
from websockets import ConnectionClosedError
from websockets.server import WebSocketServer, WebSocketServerProtocol

from socketd.core.Channel import Channel
from socketd.core.ChannelDefault import ChannelDefault
from socketd.core.Costants import Flag
from socketd.core.module.Frame import Frame
from loguru import logger

from socketd_websocket.IWebSocketServer import IWebSocketServer

log = logger.opt()


class AIOWebSocketServerImpl(WebSocketServerProtocol, IWebSocketServer):

    def __init__(self, ws_handler, ws_server: WebSocketServer, ws_aio_server: 'WsAioServer', *args, **kwargs):
        self.__loop = asyncio.get_event_loop()
        self.ws_aio_server = ws_aio_server
        self.__ws_server: WebSocketServer = ws_server
        self.__attachment = None
        WebSocketServerProtocol.__init__(self, self.on_message, self.__ws_server, *args, **kwargs)

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
        channel = ChannelDefault(conn, self.ws_aio_server.get_config(),
                                 self.ws_aio_server.get_assistant())
        self.set_attachment(channel)

    async def on_error(self, conn: 'AIOWebSocketServerImpl', ex: Exception):
        try:
            channel: Channel = conn.get_attachment()
            if channel is not None:
                # 有可能未 onOpen，就 onError 了；此时通道未成
                self.ws_aio_server.get_processor().onError(channel.get_session(), ex)
        except Exception as e:
            log.error(e)

    async def on_message(self, conn: 'AIOWebSocketServerImpl', path: str):
        """ws_handler"""
        while True:
            try:
                if conn.closed:
                    break
                message = await conn.recv()
                log.debug(message)
                frame: Frame = self.ws_aio_server.get_assistant().read(
                    message)
                # # 采用线程池执行IO耗时任务
                # frame: Frame = await self.__loop.run_in_executor(self.ws_aio_server.get_config(
                # ).get_executor(), lambda _message: self.ws_aio_server.get_assistant().read( _message), message)
                if frame is not None:
                    await self.ws_aio_server.get_process().on_receive(self.get_attachment(), frame)
                    if frame.get_flag() == Flag.Close:
                        """客户端主动关闭"""
                        await conn.close()
                        log.debug("{sessionId} 主动退出",
                                  sessionId=conn.get_attachment().get_session().get_session_id())
                        break
            except asyncio.CancelledError as c:
                logger.warning(c)
            except ConnectionClosedError as e:
                # 客户端异常关闭
                log.error(e)
            except Exception as e:
                log.error(e)
                break
            # raise e

    async def on_close(self, conn: 'WebSocketServerProtocol'):
        """关闭tcp,结束握手"""
        await conn.close()
