import asyncio
from asyncio import CancelledError
from typing import Optional, Sequence
from loguru import logger
from websockets.extensions import ClientExtensionFactory
from websockets.uri import WebSocketURI

from socketd.exception.SocketdExecption import SocketdConnectionException
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.transport.core.internal.ChannelDefault import ChannelDefault
from websockets import WebSocketClientProtocol, Origin, Subprotocol, HeadersLike

from socketd.transport.core.Costants import Flag
from socketd.transport.core.Frame import Frame
from socketd.transport.utils.CompletableFuture import CompletableFuture
from socketd_websocket import WsAioClient

log = logger.opt()


class AIOWebSocketClientImpl(WebSocketClientProtocol):
    def __init__(self, client: WsAioClient, message_loop, *args, **kwargs):
        WebSocketClientProtocol.__init__(self, *args, **kwargs)
        self.status_state = Flag.Unknown
        self.client = client
        self.channel = ChannelDefault(self, client)
        self.__loop: Optional[asyncio.AbstractEventLoop] = None
        if message_loop := message_loop:
            self.__loop = message_loop
        else:
            self.__loop = asyncio.get_running_loop()
        self.handshake_future: Optional[CompletableFuture] = None

    def get_channel(self):
        return self.channel

    def set_loop(self, loop):
        self.__loop = loop

    async def handshake(self, wsuri: WebSocketURI, origin: Optional[Origin] = None,
                        available_extensions: Optional[Sequence[ClientExtensionFactory]] = None,
                        available_subprotocols: Optional[Sequence[Subprotocol]] = None,
                        extra_headers: Optional[HeadersLike] = None) -> None:
        """开始握手"""
        return_data = await super().handshake(wsuri, origin, available_extensions, available_subprotocols,
                                              extra_headers)
        self.handshake_future: Optional[CompletableFuture] = CompletableFuture()
        await self.on_open()
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

            while True:
                await asyncio.sleep(0)
                if self.closed or self.status_state == Flag.Close:
                    break
                try:
                    await self.on_message()
                except Exception as e:
                    log.warning(e)
                    # raise e

        asyncio.run_coroutine_threadsafe(_handler(), asyncio.get_running_loop())

    async def on_open(self):
        try:
            log.info("Client:Websocket onOpen...")
            await self.channel.send_connect(self.client.get_config().get_url())
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
            # if self.status_state != Flag.Connect and not self.transfer_data_task.done():
            #     return
            message = await self.recv()
            if message is None:
                # 结束握手
                return
            frame: Frame = self.client.get_assistant().read(message)
            if frame is not None:
                self.status_state = frame.get_flag()
                if frame.get_flag() == Flag.Connack:
                    async def __future(b: bool, e: Exception):
                        if e:
                            self.handshake_future.set_e(e)
                            self.handshake_future.cancel()
                        else:
                            self.handshake_future.accept(ClientHandshakeResult(self.channel, None))

                    await self.channel.on_open_future(__future)
                # if self.channel.get_config().get_is_thread():
                #     # todo 开启单独线程后，在open确认连接后，会停留10s(线程可见性不佳)，但是可以解决线程阻塞问题
                #     asyncio.run_coroutine_threadsafe(self.client.get_processor().on_receive(self.channel, frame),
                #                                      self.__loop)
                # else:
                await self.client.get_processor().on_receive(self.channel, frame)
                if frame.get_flag() == Flag.Close:
                    """服务端主动关闭"""
                    await self.close()
                    log.debug("{sessionId} 服务端主动关闭",
                              sessionId=self.channel.get_session().get_session_id())
        except CancelledError as c:
            # 超时自动推出
            log.debug(c)
            raise c
        except SocketdConnectionException as s:
            self.handshake_future.accept(ClientHandshakeResult(self.channel, s))
            logger.warning(s)
        except Exception as e:
            self.on_error(e)
            raise e

    def on_close(self):
        self.client.get_processor().on_close(self.channel)

    def on_error(self, e):
        self.client.get_processor().on_error(self.channel, e)
