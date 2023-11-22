import time
from abc import ABC

from socketd.core.Channel import Channel
from socketd.core.Frames import Frames


class ChannelBase(Channel, ABC):
    def __init__(self, config):
        self.config = config
        self.requests = 0
        self.handshake = None
        self.live_time = 0
        self.attachments = {}
        self._is_closed = False

    def get_attachment(self, name):
        return self.attachments.get(name, None)

    def set_attachment(self, name, val):
        self.attachments[name] = val

    def is_closed(self):
        return self._is_closed

    def get_requests(self):
        return self.requests

    def set_handshake(self, handshake):
        self.handshake = handshake

    def get_handshake(self):
        return self.handshake

    def set_live_time(self):
        self.live_time = int(time.time() * 1000)

    def get_live_time(self):
        return self.live_time

    async def send_connect(self, uri):
        await self.send(Frames.connectFrame(self.config.get_id_generator()().__str__(), uri), None)

    async def send_connack(self, connect_message):
        await self.send(Frames.connackFrame(connect_message), None)

    async def send_ping(self):
        await self.send(Frames.pingFrame(), None)

    async def send_pong(self):
        await self.send(Frames.pongFrame(), None)

    async def send_close(self):
        await self.send(Frames.closeFrame(), None)

    async def close(self, code: int = 1000,
                    reason: str = "", ):
        self._is_closed = True
        self.attachments.clear()

    def get_config(self) -> 'Config':
        return self.config

