from typing import Callable

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Processor import Processor
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.impl.ProcessorDefault import ProcessorDefault
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.Config import Config


class ServerBase(Server):
    """
    服务端基类
    """

    def __init__(self, config:ServerConfig, assistant:ChannelAssistant):
        self._config = config
        self._assistant = assistant

        self._processor: Processor = ProcessorDefault()
        self.isStarted = False

    def get_assistant(self):
        """
        获取通道助理
        """
        return self._assistant

    def get_config(self) -> ServerConfig:
        return self._config

    def config(self, consumer: Callable[[ServerConfig], None]):
        """
        获取配置
        """
        consumer(self._config)
        return self


    def get_processor(self) -> Processor:
        return self._processor

    def listen(self, listener: Listener):
        """
        设置监听器
        """
        if listener is not None:
            self._processor.set_listener(listener)
        return self
