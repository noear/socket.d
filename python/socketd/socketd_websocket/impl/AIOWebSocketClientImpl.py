from loguru import logger
from websockets.protocol import State

from socketd.core.ChannelDefault import ChannelDefault
from websockets import WebSocketClientProtocol

log = logger.opt()


class AIOWebSocketClientImpl(WebSocketClientProtocol):
    def __init__(self, client, *args, **kwargs):
        WebSocketClientProtocol.__init__(self, *args, **kwargs)
        self.client = client
        self.channel = ChannelDefault(self, client.get_config(), client.assistant())
        self.status_state = State.CONNECTING

    def get_channel(self):
        return self.channel

    def connection_open(self) -> None:
        """打开握手完成回调"""
        super().connection_open()
        self.on_open()
        log.debug("AIOWebSocketClientImpl 打开握手完成回调")

    def on_open(self):
        log.info("Client:Websocket onOpen...")
        try:
            self.loop.run_until_complete(self.channel.send_connect(self.client.get_config().get_url()))
        except Exception as e:
            log.warning(str(e), exc_info=True)
            raise e

    def on_message(self, data: bytes):
        """处理消息"""
        try:
            frame = self.client.get_assistant().read(data)

            if frame is not None:
                self.client.processor().onReceive(self.channel, frame)
        except Exception as e:
            log.warning(str(e), exc_info=True)

    def on_close(self):
        self.client.processor().on_close(self.channel.get_session())

    def on_error(self, e):
        self.client.processor().on_error(self.channel.get_session(), e)
