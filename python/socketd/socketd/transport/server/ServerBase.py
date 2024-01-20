from typing import Callable

from socketd.transport.server.Server import Server
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.impl.ProcessorDefault import ProcessorDefault
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.Config import Config


class ServerBase(Server):
    """
    服务端基类
    """

    def __init__(self, config, assistant):
        self._processor = ProcessorDefault()
        self._config: ServerConfig = config
        self._assistant: ChannelAssistant = assistant
        self.isStarted = False

    def get_assistant(self):
        """
        获取通道助理
        """
        return self._assistant

    def config(self, consumer: Callable[[Config], Config]):
        """
        获取配置
        """
        consumer(self._config)
        return self

    def get_config(self):
        return self._config

    def process(self, processor):
        """
        设置处理器
        """
        if processor is not None:
            self._processor = processor
        return self

    def get_processor(self):
        return self._processor

    def listen(self, listener):
        """
        设置监听器
        """
        self._processor.set_listener(listener)
        return self
