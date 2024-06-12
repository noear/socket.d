import traceback
from typing import Optional

from socketd.transport.core.entity.MessageBuilder import MessageBuilder
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.core.impl.SessionBase import SessionBase
from socketd.transport.core.Channel import Channel
from socketd.transport.core.HandshakeDefault import HandshakeDefault
from socketd.transport.core.Entity import Entity
from socketd.transport.core.Message import Message
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Flags import Flags
from socketd.transport.stream.RequestStream import RequestStream
from socketd.transport.stream.SendStream import SendStream

from socketd.transport.stream.SubscribeStream import SubscribeStream

from socketd.transport.stream.impl.RequestStreamImpl import RequestStreamImpl
from socketd.transport.stream.impl.SendStreamImpl import SendStreamImpl
from socketd.transport.stream.impl.SubscribeStreamImpl import SubscribeStreamImpl
from socketd.utils.LogConfig import log
from socketd.utils.RunUtils import RunUtils


class SessionDefault(SessionBase):

    def __init__(self, channel: Channel):
        super().__init__(channel)
        self.__path_new = None

    def is_valid(self) -> bool:
        return self._channel.is_valid()

    def is_closing(self) -> bool:
        return self._channel.is_closing()

    def remote_address(self) -> str:
        return self._channel.get_remote_address()

    def local_address(self) -> str:
        return self._channel.get_local_address()

    def handshake(self) -> HandshakeDefault:
        return self._channel.get_handshake()

    async def send_ping(self):
        await self._channel.send_ping()

    def send(self, topic: str, content: Entity) -> SendStream:
        message = MessageBuilder().sid(self.generate_id()).event(topic).entity(content).build()

        stream: SendStream = SendStreamImpl(message.sid())
        RunUtils.taskTry(self._channel.send(Frame(Flags.Message, message), stream))
        return stream

    def send_and_request(self, event: str, content: Entity, timeout: float|None = 0) -> RequestStream:
        if timeout is None:
            timeout = 0

        if timeout < 0:
            timeout = self._channel.get_config().get_stream_timeout()

        if timeout == 0:
            timeout = self._channel.get_config().get_request_timeout()

        message = MessageBuilder().sid(self.generate_id()).event(event).entity(content).build()
        stream:RequestStream = RequestStreamImpl(message.sid(), timeout)
        RunUtils.taskTry(self._channel.send(Frame(Flags.Request, message), stream))
        return stream

    def send_and_subscribe(self, event: str, content: Entity, timeout: float = 0):
        if timeout is None:
            timeout = 0

        if timeout <= 0:
            timeout = self._channel.get_config().get_stream_timeout()

        message = MessageBuilder().sid(self.generate_id()).event(event).entity(content).build()
        stream:SubscribeStream = SubscribeStreamImpl(message.sid(), timeout)
        RunUtils.taskTry(self._channel.send(Frame(Flags.Subscribe, message), stream))
        return stream

    async def reply(self, from_msg: Message, content: Entity):
        message = MessageBuilder().sid(from_msg.sid()).event(from_msg.event()).entity(content).build()

        await self._channel.send(Frame(Flags.Reply, message), None)

    async def reply_end(self, from_msg: Message, content: Entity):
        message = MessageBuilder().sid(from_msg.sid()).event(from_msg.event()).entity(content).build()

        await self._channel.send(Frame(Flags.ReplyEnd, message), None)

    async def preclose(self):
        log.debug(
            f"{self._channel.get_config().get_role_name()} session close starting, sessionId={self.session_id()}")
        if self._channel.is_valid():
            await self._channel.send_close(Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING)

    async def close(self):
        if self._channel.is_valid():
            try:
                await self._channel.send_close(Constants.CLOSE1001_PROTOCOL_CLOSE)
            except Exception as e:
                e_msg = traceback.format_exc()
                log.warning(f" {self._channel.get_config().get_role_name()} channel send_close error \n{e_msg}")
        await self._channel.close(Constants.CLOSE2009_USER)

    def param(self, name: str):
        return self.handshake().param(name)

    def path_new(self, path: str):
        self.__path_new = path

    async def reconnect(self):
        await self._channel.reconnect()

    def path(self) -> Optional[str]:
        if path_new := self.__path_new:
            return path_new
        return self.handshake().uri().__str__()

    def param_or_default(self, name: str, defVal: str) -> str:
        return self.handshake().param_or_default(name, defVal)

    async def send_alarm(self, _from: Message, alarm: str|Entity) -> None:
        if isinstance(alarm, str):
            await self._channel.send_alarm(_from, StringEntity(alarm))
        else:
            await self._channel.send_alarm(_from, alarm)

    async def send_pressure(self, _from: Message, pressure: Entity) -> None:
        await self._channel.send_pressure(_from, pressure)


