import asyncio
from asyncio import CancelledError
from threading import Thread
from typing import Optional, Sequence
from loguru import logger
from websockets.extensions import ClientExtensionFactory
from websockets.uri import WebSocketURI

from socketd.core.ChannelDefault import ChannelDefault
from websockets import WebSocketClientProtocol, Origin, Subprotocol, HeadersLike

from socketd.core.Costants import Flag
from socketd.core.module.Frame import Frame
from socketd.transport.client.Client import Client

log = logger.opt()


class AIOWebSocketClientImpl(WebSocketClientProtocol):
    def __init__(self, client: Client, *args, **kwargs):
        WebSocketClientProtocol.__init__(self, *args, **kwargs)
        self.status_state = Flag.Unknown
        self.client = client
        self.channel = ChannelDefault(self, client.get_config(), client.get_assistant())
        self.connect_read_thread: Thread = None

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
        await self.on_open()
        return return_data

    def connection_open(self) -> None:
        """打开握手完成回调"""
        super().connection_open()
        log.debug("AIOWebSocketClientImpl connection_open")

        async def _handler():
            while True:
                await asyncio.sleep(0)
                if self.closed or self.status_state == Flag.Close:
                    break
                if self.status_state != Flag.Unknown:
                    try:
                        await self.on_message()
                    except Exception as e:
                        break

        def thread__handler(_loop):
            asyncio.set_event_loop(_loop)
            _loop.run_until_complete(_handler())

        if self.client.get_config().get_is_thread():
            loop = asyncio.new_event_loop()
            if self.connect_read_thread is None:
                self.connect_read_thread = Thread(target=thread__handler, args=(loop,))
                self.connect_read_thread.start()
        else:
            asyncio.run_coroutine_threadsafe(_handler(), asyncio.get_event_loop())

    async def on_open(self):
        log.info("Client:Websocket onOpen...")
        try:
            await self.channel.send_connect(self.client.get_config().get_url())
            await self.on_message()
        except Exception as e:
            log.warning(str(e), exc_info=True)
            raise e

    async def on_message(self):
        """处理消息"""
        try:
            message = await self.recv()
            log.debug(message)
            frame: Frame = self.client.get_assistant().read(message)
            self.status_state = frame.get_flag()
            if frame is not None:
                await self.client.get_processor().on_receive(self.channel, frame)
                if frame.get_flag() == Flag.Close:
                    """服务端主动关闭"""
                    await self.close()
                    log.debug("{sessionId} 主动退出",
                              sessionId=self.channel.get_session().get_session_id())
        except CancelledError as c:
            # 超时自动推出
            log.debug(c)
        except Exception as e:
            log.warning(str(e), exc_info=True)
            raise e

    def on_close(self):
        self.client.get_processor().on_close(self.channel.get_session())

    def on_error(self, e):
        self.client.get_processor().on_error(self.channel.get_session(), e)
