import asyncio
from asyncio import CancelledError
from typing import Optional, Sequence
from loguru import logger
from websockets.extensions import ClientExtensionFactory
from websockets.uri import WebSocketURI

from socketd.exception.SocketDExecption import SocketDConnectionException
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.transport.core.impl.LogConfig import log
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from websockets import WebSocketClientProtocol, Origin, Subprotocol, HeadersLike, ConnectionClosedOK

from socketd.transport.core.Costants import Flag
from socketd.transport.core.Frame import Frame
from socketd.transport.utils.CompletableFuture import CompletableFuture
from socketd_websocket import WsAioClient


class AIOWebSocketClientImpl(WebSocketClientProtocol):
    def __init__(self, client: WsAioClient, message_loop, *args, **kwargs):
        WebSocketClientProtocol.__init__(self, *args, **kwargs)
        self.status_state = Flag.Unknown
        self.client = client
        self.channel = ChannelDefault(self, client)
        # 预留的新的事件循环，用于跨线程操作
        self._loop: Optional[asyncio.AbstractEventLoop] = None
        if message_loop := message_loop:
            self._loop = message_loop
        else:
            self._loop = asyncio.get_running_loop()
        self.handshake_future: Optional[CompletableFuture] = None

    def get_channel(self):
        return self.channel

    async def handshake(self, wsuri: WebSocketURI, origin: Optional[Origin] = None,
                        available_extensions: Optional[Sequence[ClientExtensionFactory]] = None,
                        available_subprotocols: Optional[Sequence[Subprotocol]] = None,
                        extra_headers: Optional[HeadersLike] = None) -> None:
        """开始握手"""
        return_data = await super().handshake(wsuri, origin, available_extensions, available_subprotocols,
                                              extra_headers)
        self.handshake_future: Optional[CompletableFuture] = CompletableFuture()
        return return_data

    def connection_open(self) -> None:
        """
        打开握手完成回调函数。
        :return: 无返回值
        """
        super().connection_open()

        async def _handler():
            """
            异步处理函数，用于处理握手完成后的消息处理逻辑。
            """
            await self.on_open()
            while True:
                await asyncio.sleep(0)
                if self.closed or self.status_state == Flag.Close:
                    break
                try:
                    await self.on_message()
                except Exception as e:
                    log.warning(e)
                    raise e

        asyncio.run_coroutine_threadsafe(_handler(), self.loop)

    async def on_open(self):
        try:
            log.info("Client:Websocket onOpen...")
            await self.channel.send_connect(self.client.get_config().get_url(), self.client.get_config().get_meta())
            while self.status_state == Flag.Connect:
                await self.on_message()
        except Exception as e:
            log.error(str(e), exc_info=True)
            raise e

    async def on_message(self):
        """处理消息"""
        if self.status_state == Flag.Close:
            return
        try:
            message = await self.recv()
            if message is None:
                # 结束握手
                return
            # frame: Frame = self.client.get_assistant().read(message)
            frame: Frame = await self.loop.run_in_executor(None,
                                                           lambda _message: self.client.get_assistant().read(_message),
                                                           message)
            if frame is not None:
                self.status_state = frame.get_flag()
                if frame.get_flag() == Flag.Connack:
                    async def __future(b: bool, _e: Exception):
                        if _e:
                            self.handshake_future.set_e(_e)
                            self.handshake_future.cancel()
                        else:
                            self.handshake_future.accept(ClientHandshakeResult(self.channel, None))

                    await self.channel.on_open_future(__future)
                # 将on_receive 让新的事件循环进行回调，不阻塞当前read循环
                asyncio.run_coroutine_threadsafe(self.client.get_processor().on_receive(self.channel, frame), self.loop)
                if frame.get_flag() == Flag.Close:
                    """服务端主动关闭"""
                    # await self.close()
                    log.debug("{sessionId} 服务端主动关闭",
                              sessionId=self.channel.get_session().get_session_id())
        except CancelledError as c:
            # 超时自动推出
            log.debug(c)
            raise c
        except SocketDConnectionException as s:
            self.handshake_future.accept(ClientHandshakeResult(self.channel, s))
            logger.warning(s)
        except ConnectionClosedOK as e:
            logger.info(e)
        except Exception as e:
            self.on_error(e)

    def on_close(self):
        self.client.get_processor().on_close(self.channel)

    def on_error(self, e):
        self.client.get_processor().on_error(self.channel, e)
