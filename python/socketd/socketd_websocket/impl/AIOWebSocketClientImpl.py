import asyncio
from asyncio import CancelledError
from threading import Thread
from typing import Optional, Sequence
from loguru import logger
from websockets.extensions import ClientExtensionFactory
from websockets.uri import WebSocketURI

from socketd.transport.core.internal.ChannelDefault import ChannelDefault
from websockets import WebSocketClientProtocol, Origin, Subprotocol, HeadersLike

from socketd.transport.core.Costants import Flag
from socketd.transport.core.Frame import Frame
from socketd.transport.utils.AsyncUtil import AsyncUtil
from socketd_websocket import WsAioClient

log = logger.opt()


class AIOWebSocketClientImpl(WebSocketClientProtocol):
    def __init__(self, client: WsAioClient, *args, **kwargs):
        WebSocketClientProtocol.__init__(self, *args, **kwargs)
        self.status_state = Flag.Unknown
        self.client = client
        self.channel = ChannelDefault(self, client)
        self.connect_read_thread: Thread | None = None

    def get_channel(self):
        return self.channel

    async def handshake(self, wsuri: WebSocketURI, origin: Optional[Origin] = None,
                        available_extensions: Optional[Sequence[ClientExtensionFactory]] = None,
                        available_subprotocols: Optional[Sequence[Subprotocol]] = None,
                        extra_headers: Optional[HeadersLike] = None) -> None:
        """开始握手"""
        return_data = await super().handshake(wsuri, origin, available_extensions, available_subprotocols,
                                              extra_headers)
        log.debug("AIOWebSocketClientImpl handshake")
        self.status_state = Flag.Connect
        await self.on_open()
        return return_data

    def connection_open(self) -> None:
        """
        打开握手完成回调函数。
        :return: 无返回值
        """
        super().connection_open()
        log.debug("AIOWebSocketClientImpl connection_open")

        async def _handler():
            """
            异步处理函数，用于处理握手完成后的消息处理逻辑。
            """
            while True:
                await asyncio.sleep(0)
                if self.closed or self.status_state == Flag.Close:
                    break
                if self.status_state > Flag.Connect:
                    try:
                        await self.on_message()
                    except Exception as e:
                        log.warning(e)
                        # raise e

        # 如果配置中设置了使用线程，则创建一个新的事件循环，并启动一个线程来处理读取操作
        if self.client.get_config().get_is_thread():
            loop = asyncio.new_event_loop()
            if self.connect_read_thread is None:
                self.connect_read_thread = Thread(target=AsyncUtil.thread_handler, args=(loop, loop.create_task(_handler())))
                self.connect_read_thread.start()
        else:
            # 否则，使用异步运行此协程，并在当前线程中运行。
            asyncio.run_coroutine_threadsafe(_handler(), asyncio.get_event_loop())

    async def on_open(self):
        try:
            log.info("Client:Websocket onOpen...")
            await self.channel.send_connect(self.client.get_config().get_url())
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
            frame: Frame = self.client.get_assistant().read(message)
            if frame is not None:
                self.status_state = frame.get_flag()
                await self.client.get_processor().on_receive(self.channel, frame)
                if frame.get_flag() == Flag.Close:
                    """服务端主动关闭"""
                    await self.close()
                    log.debug("{sessionId} 服务端主动关闭",
                              sessionId=self.channel.get_session().get_session_id())
        except CancelledError as c:
            # 超时自动推出
            log.debug(c)
            # raise c
        except Exception as e:
            self.on_error(e)
            # raise e

    def on_close(self):
        self.client.get_processor().on_close(self.channel)

    def on_error(self, e):
        self.client.get_processor().on_error(self.channel, e)

