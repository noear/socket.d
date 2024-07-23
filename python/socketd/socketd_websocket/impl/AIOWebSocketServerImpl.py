from __future__ import annotations
import asyncio
from typing import Optional, Union, Sequence

from websockets import ConnectionClosedOK, ConnectionClosedError, Subprotocol, Headers, InvalidOrigin
from websockets.server import WebSocketServer, WebSocketServerProtocol

from socketd import SocketD
from socketd.transport.core.Channel import Channel
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.utils.LogConfig import log
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Frame import Frame


class AIOWebSocketServerImpl(WebSocketServerProtocol):

    def __init__(self, ws_server: WebSocketServer, ws_aio_server: 'WsAioServer',
                 *args, **kwargs):
        self.ws_aio_server = ws_aio_server
        self.__ws_server: WebSocketServer = ws_server
        self.__attachment: Optional[Channel] = None
        WebSocketServerProtocol.__init__(self=self,
                                         ws_handler=self.on_message,
                                         ws_server=self.__ws_server,
                                         *args,
                                         **kwargs)

    def process_subprotocol(self, headers: Headers, available_subprotocols: Optional[Sequence[Subprotocol]]) \
            -> Optional[Subprotocol]:
        header_values = headers.get_all("Sec-WebSocket-Protocol")

        if self.ws_aio_server.get_config().is_use_subprotocols():
            # 开启子协议验证的时候，如果不匹配则拒绝握手
            if bool(header_values) and header_values.__contains__(SocketD.protocol_name()):
                return Subprotocol(SocketD.protocol_name())
            else:
                raise InvalidOrigin("No subprotocols supported")
        else:
            if bool(header_values) and header_values.__contains__(SocketD.protocol_name()):
                return Subprotocol(SocketD.protocol_name())

        return super().process_subprotocol(headers, available_subprotocols)


    def set_attachment(self, obj: ChannelInternal):
        self.__attachment = obj

    def get_attachment(self) -> ChannelInternal:
        return self.__attachment

    def connection_open(self) -> None:
        """握手完成回调"""
        super().connection_open()
        self.on_open(self)

    async def keepalive_ping(self) -> None:
        await asyncio.sleep(self.ping_interval)
        if await self.assert_handshake():
            return await super().keepalive_ping()

    def on_open(self, conn) -> None:
        """create_protocol"""
        if self.get_attachment() is None:
            channel = ChannelDefault(conn, self.ws_aio_server)
            self.set_attachment(channel)

    def on_close(self, conn) -> None:
        channel: ChannelInternal = conn.get_attachment()
        if channel is not None:
            # 有可能未 onOpen，就 onClose 了；此时通道未成
            self.ws_aio_server.get_processor().on_close(channel)


    def on_error(self, conn: Union[AIOWebSocketServerImpl, WebSocketServerProtocol], ex: Exception):
        channel: ChannelInternal = conn.get_attachment()
        if channel is not None:
            # 有可能未 onOpen，就 onError 了；此时通道未成
            self.ws_aio_server.get_processor().on_error(channel, ex)

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
                # 采用线程池执行IO耗时任务
                frame: Frame = await loop.run_in_executor(self.ws_aio_server.get_config().get_exchange_executor(),
                                                          lambda _message: self.ws_aio_server.get_assistant().read(
                                                              _message), message)
                if frame is not None:
                    # 不等待直接运行
                    tasks.append(
                        loop.create_task(self.ws_aio_server.get_processor().reve_frame(self.get_attachment(), frame)))
                    if frame.flag() == Flags.Close:
                        # 不需要再 while(true) 了 //其它处理在 processor
                        break


            except asyncio.CancelledError as e:
                break
            except ConnectionClosedOK as e:
                self.on_close(conn)
            except ConnectionClosedError as e:
                self.on_close(conn)
            except Exception as e:
                self.on_error(conn, e)

    # 未签名前，禁止 ping/pong
    async def assert_handshake(self) -> bool:
        channel: ChannelInternal = self.get_attachment()
        if channel is None or channel.get_handshake() is None:
            try:
                await self.close()
            except Exception:
                ...
            log.warning("Server channel no handshake onPingPong")
            return False
        else:
            return True
