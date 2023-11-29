import asyncio

from abc import ABC

from .SessionBase import SessionBase
from .Channel import Channel
from .Handshake import Handshake
from .module.Entity import Entity
from .module.Message import Message
from .module.Frame import Frame
from .Costants import Flag, Function
from .module.MessageDefault import MessageDefault


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

    def send_and_request_with_timeout(self, topic: str, content: Entity, timeout: int) -> Entity:
        pass

    def send_ping(self):
        self.channel.send_ping()

    async def send(self, topic: str, content: Entity):
        message = MessageDefault().set_sid(self.generate_id()).set_topic(topic).set_entity(content)
        await self.channel.send(Frame(Flag.Message, message), None)

    async def send_and_request(self, topic: str, content: Entity, timeout: int) -> Entity:
        # todo 不可用
        with self.channel.get_requests() as num:
            if num > self.channel.get_config().get_max_requests():
                raise Exception("Sending too many requests: " + str(num))
            else:
                await self.channel.get_requests().set(num + 1)

        message = MessageDefault().sid(self.generate_id()).topic(topic).entity(content)

        try:
           await self.channel.send(Frame(Flag.Request, message))
        except Exception as e:
            raise Exception(e)
        finally:
            self.channel.remove_acceptor(message.getSid())
            # self.channel.get_requests().denominator()

    async def send_and_subscribe(self, topic: str, content: Entity, consumer: Function):
        message = MessageDefault().sid(self.generate_id()).topic(topic).entity(content)
        await self.channel.send(Frame(Flag.Subscribe, message), None)

    async def reply(self, from_msg: Message, content: Entity):
        await self.channel.send(Frame(Flag.Reply, MessageDefault().sid(from_msg.get_sid()).entity(content)), None)

    async def reply_end(self, from_msg: Message, content: Entity):
        await self.channel.send(Frame(Flag.ReplyEnd, MessageDefault().sid(from_msg.get_sid()).entity(content)), None)

    async def close(self):
        await self.channel.send_close()
        await self.channel.close()
