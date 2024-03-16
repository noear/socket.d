import asyncio
import time
import threading

from abc import ABC
from typing import Optional

from socketd.transport.core.Channel import Channel
from socketd.transport.core.Frames import Frames
from socketd.transport.core.Config import Config

from socketd.transport.utils.sync_api.AtomicRefer import AtomicRefer


class ChannelBase(Channel, ABC):
    def __init__(self, config: Config):
        self.closeCode: Optional[int] = 0
        self.config = config
        self.requests = AtomicRefer(0)
        self.handshake = None
        self.liveTime = 0
        self.attachments = {}
        self.__lock: threading.Lock = threading.Lock()
        self.__loop: Optional[asyncio.AbstractEventLoop] = None

    def __enter__(self):
        return self.__lock.acquire()

    def __exit__(self, exc_type, exc_val, exc_tb):
        return self.__lock.release()

    def get_attachment(self, name):
        return self.attachments.get(name, None)

    def set_attachment(self, name, val):
        self.attachments[name] = val

    def get_requests(self):
        return self.requests

    def set_handshake(self, handshake):
        self.handshake = handshake

    def get_handshake(self):
        return self.handshake

    def set_live_time(self):
        self.liveTime = int(time.time() * 1000)

    def get_live_time(self):
        return self.liveTime

    async def send_connect(self, uri, metaMap: dict[str,str]):
        await self.send(Frames.connect_frame(self.config.gen_id(), uri, metaMap), None)

    async def send_connack(self, connect_message):
        await self.send(Frames.connack_frame(self.get_handshake()), None)

    async def send_ping(self):
        await self.send(Frames.ping_frame(), None)

    async def send_pong(self):
        await self.send(Frames.pong_frame(), None)

    async def send_close(self):
        await self.send(Frames.close_frame(), None)

    async def close(self, code: int):
        self.closeCode = code
        self.attachments.clear()

    def get_config(self) -> Config:
        return self.config

    def is_valid(self) -> bool:
        pass

    def get_loop(self) -> asyncio.AbstractEventLoop:
        return self.__loop

    def set_loop(self, loop) -> None:
        self.__loop = loop

