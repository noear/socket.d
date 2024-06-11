import asyncio
import threading

from abc import ABC
from typing import Optional

from socketd.transport.core import Entity
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Frames import Frames
from socketd.transport.core.Config import Config
from socketd.transport.core.HandshakeDefault import HandshakeInternal
from socketd.transport.core.Message import Message
from socketd.utils.MapUtils import MapUtils


class ChannelBase(Channel, ABC):
    def __init__(self, config: Config):
        self.config = config

        self.handshake: HandshakeInternal = None
        self.__attachments:dict = {}

        self.__lock: threading.Lock = threading.Lock()
        self.__loop: Optional[asyncio.AbstractEventLoop] = None

    def __enter__(self):
        return self.__lock.acquire()

    def __exit__(self, exc_type, exc_val, exc_tb):
        return self.__lock.release()

    def get_config(self) -> Config:
        return self.config

    def is_valid(self) -> bool:
        pass

    def get_loop(self) -> asyncio.AbstractEventLoop:
        return self.__loop

    def set_loop(self, loop) -> None:
        self.__loop = loop

    def get_attachment(self, name):
        return self.__attachments.get(name, None)

    def put_attachment(self, name, val):
        if val is None:
            MapUtils.remove(self.__attachments, name)
        else:
            self.__attachments[name] = val

    def set_handshake(self, handshake):
        if handshake is not None:
            self.handshake = handshake

    def get_handshake(self):
        return self.handshake

    async def send_connect(self, uri, metaMap: dict[str, str]):
        await self.send(Frames.connect_frame(self.config.gen_id(), uri, metaMap), None)

    async def send_connack(self, connect_message):
        await self.send(Frames.connack_frame(self.get_handshake()), None)

    async def send_ping(self):
        await self.send(Frames.ping_frame(), None)

    async def send_pong(self):
        await self.send(Frames.pong_frame(), None)

    async def send_close(self, code: int):
        await self.send(Frames.close_frame(code), None)

    async def send_alarm(self, _from: Message, alarm: Entity):
        await self.send(Frames.alarm_frame(_from, alarm), None)

    async def send_pressure(self, _from: Message, pressure: Entity):
        await self.send(Frames.pressure_frame(_from, pressure), None)

    async def close(self, code: int):
        if code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING:
            self.__attachments.clear()

