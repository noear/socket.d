from __future__ import annotations
import asyncio
import traceback
from typing import Optional, Union

from websockets import ConnectionClosedOK
from websockets.frames import Opcode
from websockets.server import WebSocketServer, WebSocketServerProtocol

from socketd.transport.core.Channel import Channel
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.utils.LogConfig import log
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Frame import Frame


class AIOWebSocketServerImpl(WebSocketServerProtocol):

    def __init__(self, ws_handler, ws_server: WebSocketServer, ws_aio_server: 'WsAioServer',
                 *args, **kwargs):
        self.ws_aio_server = ws_aio_server
        self.__ws_server: WebSocketServer = ws_server
        self.__attachment: Optional[Channel] = None
        WebSocketServerProtocol.__init__(self=self,
                                         ws_handler=self.on_message,
                                         ws_server=self.__ws_server,
                                         *args,
                                         **kwargs)

    def set_attachment(self, obj: ChannelInternal):
        self.__attachment = obj

    def get_attachment(self) -> ChannelInternal:
        return self.__attachment

    def connection_open(self) -> None:
        """握手完成回调"""
        super().connection_open()
        self.on_open(self)

    async def read_frame(self, max_size: Optional[int]) -> Frame:
        frame = await super().read_frame(max_size)
        if frame is not None:
            if frame.opcode == Opcode.PONG:
                await self.assert_handshake()
        return frame

    def on_open(self, conn) -> None:
        """create_protocol"""
        if self.get_attachment() is None:
            channel = ChannelDefault(conn, self.ws_aio_server)
            self.set_attachment(channel)

    async def on_error(self, conn: Union[AIOWebSocketServerImpl, WebSocketServerProtocol], ex: Exception):
        try:
            channel: ChannelInternal = conn.get_attachment()
            if channel is not None:
                # 有可能未 onOpen，就 onError 了；此时通道未成
                self.ws_aio_server.get_processor().on_error(channel, ex)
        except Exception as e:
            e_msg = traceback.format_exc()
            log.warning(e_msg)

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
                        loop.create_task(self.ws_aio_server.get_processor().on_receive(self.get_attachment(), frame)))
                    if frame.flag() == Flags.Close:
                        # 不需要再 while(true) 了 //其它处理在 processor
                        break


            except asyncio.CancelledError as e:
                break
            except ConnectionClosedOK as e:
                break
            except Exception as e:
                await self.on_error(conn, e)

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
