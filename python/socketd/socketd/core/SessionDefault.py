import asyncio

from abc import ABC
from typing import Callable, Awaitable, Any, Coroutine

from .SessionBase import SessionBase
from .Channel import Channel
from .Handshake import Handshake
from .module.Entity import Entity
from .module.Message import Message
from .module.Frame import Frame
from .Costants import Flag
from .module.MessageDefault import MessageDefault
from ..exception.SocketdExecption import SocketDException
from ..transport.core.CompletableFuture import CompletableFuture
from ..transport.core.StreamRequest import StreamRequest
from ..transport.core.StreamSubscribe import StreamSubscribe


class SessionDefault(SessionBase, ABC):

    def __init__(self, channel: Channel):
        super().__init__(channel)
        self.__loop = asyncio.get_event_loop()

    def is_valid(self) -> bool:
        return self.channel.is_valid()

    def get_remote_address(self) -> str:
        return self.channel.get_remote_address()

    def get_local_address(self) -> str:
        return self.channel.get_local_address()

    def get_handshake(self) -> Handshake:
        return self.channel.get_handshake()

    async def send_ping(self):
        await self.channel.send_ping()

    async def send(self, topic: str, content: Entity):
        message = MessageDefault().set_sid(self.generate_id()).set_event(topic).set_entity(content)
        await self.channel.send(Frame(Flag.Message, message), None)

    async def send_and_request(self, event: str, content: Entity,
                               timeout: int = 100) -> Entity:

        if timeout < 100:
            timeout = self.channel.get_config().get_reply_timeout()
        future: CompletableFuture[Entity] = CompletableFuture()
        message = MessageDefault().set_sid(self.generate_id()).set_event(event).set_entity(content)
        try:
            await self.channel.send(Frame(Flag.Request, message),
                                    StreamRequest(message.get_sid(), timeout, future))
            return await future.get(timeout)
        except asyncio.TimeoutError as e:
            if self.channel.is_valid():
                raise SocketDException(f"Request reply timeout>{timeout} "
                                f"sessionId={self.channel.get_session().get_session_id()} "
                                f"event={event} sid={message.get_sid()}")
            else:
                raise SocketDException(f"This channel is closed sessionId={self.channel.get_session().get_session_id()} "
                                f"event={event} sid={message.get_sid()}")
        except Exception as e:
            raise e
        finally:
            self.channel.remove_acceptor(message.get_sid())

    async def send_stream_and_request(self, event: str, content: Entity,
                                      consumer: Callable[[Entity], Awaitable[Any]] | Coroutine[Entity, Any, None],
                                      timeout: int):
        message = MessageDefault().set_sid(self.generate_id()).set_event(event).set_entity(content)
        future: CompletableFuture[Entity] = CompletableFuture()
        try:
            if asyncio.iscoroutinefunction(consumer):
                await consumer(content)
            else:
                self.channel.get_config().get_executor().submit(fn=consumer, args=(content,))
        except Exception as e:
            self.channel.on_error(e)
        streamAcceptor = StreamRequest(message.get_sid(), timeout, future)
        await self.channel.send(Frame(Flag.Request, message), streamAcceptor)
        return streamAcceptor

    async def send_and_subscribe(self, event: str, content: Entity, consumer: Callable[[Entity], Any], timeout: int):
        message = MessageDefault().set_sid(self.generate_id()).set_event(event).set_entity(content)
        streamAcceptor = StreamSubscribe(message.get_sid(), timeout, consumer)
        await self.channel.send(Frame(Flag.Subscribe, message), streamAcceptor)
        return streamAcceptor

    async def reply(self, from_msg: Message, content: Entity):
        await self.channel.send(Frame(Flag.Reply,
                                      MessageDefault()
                                      .set_sid(from_msg.get_sid())
                                      .set_event(from_msg.get_event())
                                      .set_entity(content)), None)

    async def reply_end(self, from_msg: Message, content: Entity):
        await self.channel.send(Frame(Flag.ReplyEnd,
                                      MessageDefault()
                                      .set_sid(from_msg.get_sid())
                                      .set_event(from_msg.get_event())
                                      .set_entity(content)), None)

    async def close(self):
        if self.channel.is_valid():
            await self.channel.send_close()
            await self.channel.close()
