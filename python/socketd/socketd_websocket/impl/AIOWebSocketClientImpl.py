import asyncio
import traceback
from asyncio import CancelledError
from typing import Optional, Sequence, List
from websockets.extensions import ClientExtensionFactory
from websockets.uri import WebSocketURI

from socketd.exception.SocketDExecption import SocketDConnectionException
from socketd.transport.client.ClientHandshakeResult import ClientHandshakeResult
from socketd.utils.LogConfig import log
from socketd.transport.core.impl.ChannelDefault import ChannelDefault
from websockets import WebSocketClientProtocol, Origin, Subprotocol, HeadersLike, ConnectionClosedOK

from socketd.transport.core.Flags import Flags
from socketd.transport.core.Frame import Frame
from socketd.utils.CompletableFuture import CompletableFuture
from socketd_websocket import WsAioClient


class AIOWebSocketClientImpl(WebSocketClientProtocol):
    def __init__(self, client: WsAioClient, message_loop, *args, **kwargs):
        WebSocketClientProtocol.__init__(self, *args, **kwargs)
        self.status_state = Flags.Unknown
        self.client = client
        self.channel = ChannelDefault(self, client)
        # 预留的新的事件循环，用于跨线程操作
        self._loop: Optional[asyncio.AbstractEventLoop] = None
        if message_loop := message_loop:
            self._loop = message_loop
        else:
            self._loop = asyncio.get_running_loop()
        self.handshake_future: Optional[CompletableFuture] = None
        self._handler_future: Optional[asyncio.Future] = None
        self.__on_receive_tasks: List[asyncio.Task] = []

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

    async def _handler(self):
        """
        异步处理函数，用于处理握手完成后的消息处理逻辑。
        """
        await self.on_open()
        while True:
            await asyncio.sleep(0)
            if self.closed or self.status_state == Flags.Close:
                break
            try:
                await self.on_message()
            except Exception as e:
                e_msg = traceback.format_exc()
                log.warning(e_msg)
                break

    def connection_open(self) -> None:
        """
        打开握手完成回调函数。
        :return: 无返回值
        """
        super().connection_open()
        self._handler_future = self.loop.create_task(self._handler())

    async def on_open(self):
        """开始建立连接"""
        try:
            log.info("Client:Websocket onOpen...")
            await self.channel.send_connect(self.client.get_config().get_url(), self.client.get_config().get_meta_map())
            while self.status_state == Flags.Connect:
                await self.on_message()
        except Exception as e:
            log.error(str(e), exc_info=True)
            raise e

    async def on_message(self):
        """处理消息"""
        if self.status_state == Flags.Close:
            return
        try:
            if self.__on_receive_tasks:
                task = self.__on_receive_tasks[0]
                if task.done():
                    self.__on_receive_tasks.pop(0)
            message = await self.recv()
            if message is None:
                # 结束握手
                return
            frame: Frame = await self.loop.run_in_executor(self.client.get_config().get_exchange_executor(),
                                                           lambda _message: self.client.get_assistant().read(_message),
                                                           message)
            if frame is not None:
                self.status_state = frame.flag()
                if frame.flag() == Flags.Connack:
                    async def __future(b: bool, _e: Exception):
                        if _e:
                            self.handshake_future.set_e(_e)
                            self.handshake_future.cancel()
                        else:
                            self.handshake_future.accept(ClientHandshakeResult(self.channel, None))
                    await self.channel.on_open_future(__future)
                # 将on_receive 让新的事件循环进行回调，不阻塞当前read循环
                self.__on_receive_tasks.append(self.loop.create_task(self.client.get_processor().on_receive(self.channel, frame)))
                if frame.flag() == Flags.Close:
                    """服务端主动关闭"""
                    log.debug("{sessionId} 服务端主动关闭",
                              sessionId=self.channel.get_session().session_id())
        except CancelledError as c:
            # 超时自动推出
            log.debug(c)
            raise c
        except SocketDConnectionException as s:
            s_msg = traceback.format_exc()
            self.handshake_future.accept(ClientHandshakeResult(self.channel, s))
            log.warning(s_msg)
        except ConnectionClosedOK as e:
            log.info(e)
        except Exception as e:
            self.on_error(e)

    async def on_close(self):
        await self.client.get_processor().on_close(self.channel)
        if self.handshake_future is not None:
            if self.__on_receive_tasks:
                await asyncio.wait(self.__on_receive_tasks, timeout=10)
            await asyncio.wait([self._handler_future], timeout=10)

    def on_error(self, e):
        self.client.get_processor().on_error(self.channel, e)
