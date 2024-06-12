from typing import Callable, Set

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Message import Message
from socketd.transport.core.Processor import Processor
from socketd.transport.core.Session import Session
from socketd.transport.core.listener.SimpleListener import SimpleListener
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.impl.ProcessorDefault import ProcessorDefault
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.utils.RunUtils import RunUtils


class ServerBase(Server,Listener):
    """
    服务端基类
    """

    def __init__(self, config:ServerConfig, assistant:ChannelAssistant):
        self._config = config
        self._assistant = assistant
        self._isStarted:bool = False

        self._processor: Processor = ProcessorDefault()
        self._sessions:Set[Session] = set()
        self._listener: Listener = SimpleListener()

        self._processor.set_listener(self)

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
            self._listener = listener
        return self

    async def prestop(self):
        await self.prestop_do()

    async def stop(self):
        await self.stop_do()

    async def on_open(self, s: Session):
        self._sessions.add(s)
        await RunUtils.waitTry(self._listener.on_open(s))

    async def on_message(self, s: Session, m: Message):
        await RunUtils.waitTry(self._listener.on_message(s, m))

    async def on_reply(self, s: Session, m: Message):
        await RunUtils.waitTry(self._listener.on_reply(s, m))

    async def on_send(self, s: Session, m: Message):
        await RunUtils.waitTry(self._listener.on_send(s, m))

    async def on_close(self, s: Session):
        self._sessions.remove(s)
        await RunUtils.waitTry(self._listener.on_close(s))

    async def on_error(self, s: Session, e:Exception):
        await RunUtils.waitTry(self._listener.on_error(s, e))

    async def prestop_do(self):
        tmp = list(self._sessions)
        for s1 in tmp:
            if s1.is_valid():
                await s1.preclose()

    async def stop_do(self):
        tmp = list(self._sessions)
        for s1 in tmp:
            if s1.is_valid():
                await s1.close()

        self._sessions.clear()
