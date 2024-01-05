import asyncio

from abc import ABC
from typing import Callable, Awaitable, Any, Coroutine, Optional

from socketd.transport.core.internal.SessionBase import SessionBase
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Handshake import Handshake
from socketd.transport.core.Entity import Entity
from socketd.transport.core.Message import Message
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Costants import Flag
from socketd.transport.core.entity.MessageDefault import MessageDefault
from socketd.exception.SocketdExecption import SocketDException
from socketd.transport.core.stream.StreamRequest import StreamRequest
from socketd.transport.core.stream.StreamSubscribe import StreamSubscribe
from socketd.transport.utils.CompletableFuture import CompletableFuture


class SessionDefault(SessionBase, ABC):

    def __init__(self, channel: Channel):
        super().__init__(channel)
        self.__path_new = None
        self.__loop = asyncio.get_event_loop()

    def is_valid(self) -> bool:
        return self._channel.is_valid()

    def get_remote_address(self) -> str:
        return self._channel.get_remote_address()

    def get_local_address(self) -> str:
        return self._channel.get_local_address()

    def get_handshake(self) -> Handshake:
        return self._channel.get_handshake()

    async def send_ping(self):
        await self._channel.send_ping()

    async def send(self, topic: str, content: Entity):
        message = MessageDefault().set_sid(self.generate_id()).set_event(topic).set_entity(content)
        await self._channel.send(Frame(Flag.Message, message), None)

    async def send_and_request(self, event: str, content: Entity,
                               timeout: int = 100) -> Entity:

        if timeout < 100:
            timeout = self._channel.get_config().get_reply_timeout()
        future: CompletableFuture[Entity] = CompletableFuture()
        message = MessageDefault().set_sid(self.generate_id()).set_event(event).set_entity(content)
        try:
            await self._channel.send(Frame(Flag.Request, message),
                                     StreamRequest(message.get_sid(), timeout, future))
            return await future.get(timeout)
        except asyncio.TimeoutError as e:
            if self._channel.is_valid():
                raise SocketDException(f"Request reply timeout>{timeout} "
                                       f"sessionId={self._channel.get_session().get_session_id()} "
                                       f"event={event} sid={message.get_sid()}")
            else:
                raise SocketDException(
                    f"This channel is closed sessionId={self._channel.get_session().get_session_id()} "
                    f"event={event} sid={message.get_sid()} {str(e)}")
        except Exception as e:
            raise e
        finally:
            self._channel.remove_acceptor(message.get_sid())

    async def send_stream_and_request(self, event: str, content: Entity,
                                      consumer: Callable[[Entity], Awaitable[Any]] | Coroutine[Entity, Any, None],
                                      timeout: int):
        message = MessageDefault().set_sid(self.generate_id()).set_event(event).set_entity(content)
        future: CompletableFuture[Entity] = CompletableFuture()
        try:
            if asyncio.iscoroutinefunction(consumer):
                await consumer(content)
            else:
                self._channel.get_config().get_executor().submit(fn=consumer, args=(content,))
        except Exception as e:
            self._channel.on_error(e)
        streamAcceptor = StreamRequest(message.get_sid(), timeout, future)
        await self._channel.send(Frame(Flag.Request, message), streamAcceptor)
        return streamAcceptor

    async def send_and_subscribe(self, event: str, content: Entity, consumer: Callable[[Entity], Any], timeout: int):
        message = MessageDefault().set_sid(self.generate_id()).set_event(event).set_entity(content)
        streamAcceptor = StreamSubscribe(message.get_sid(), timeout, consumer)
        await self._channel.send(Frame(Flag.Subscribe, message), streamAcceptor)
        return streamAcceptor

    async def reply(self, from_msg: Message, content: Entity):
        await self._channel.send(Frame(Flag.Reply,
                                       MessageDefault()
                                       .set_sid(from_msg.get_sid())
                                       .set_event(from_msg.get_event())
                                       .set_entity(content)), None)

    async def reply_end(self, from_msg: Message, content: Entity):
        await self._channel.send(Frame(Flag.ReplyEnd,
                                       MessageDefault()
                                       .set_sid(from_msg.get_sid())
                                       .set_event(from_msg.get_event())
                                       .set_entity(content)), None)

    async def reconnect(self):
        await self._channel.reconnect()

    async def close(self):
        if self._channel.is_valid():
            await self._channel.send_close()
            await self._channel.close()

    def get_param(self, name: str):
        return self.get_handshake().get_param(name)

    def pathNew(self, path: str):
        self.__path_new = path

    def path(self) -> Optional[str]:
        if path_new := self.__path_new:
            return path_new
        return self.get_handshake().get_uri().__str__()
