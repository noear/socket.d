from typing import Callable

from socketd.core.module.Message import Message
from socketd.transport.core.SteamAcceptor import StreamInternal
from asyncio.futures import Future


class StreamBase(StreamInternal):
    """流接收器基类"""

    def __init__(self, sid: str, timeout: int):
        self.__sid = sid
        self.__timeout = timeout
        self.__onError: Callable[[Exception], None] = None

    def on_error(self, error: Exception):
        if error:
            self.on_error(error)

    def get_sid(self) -> str:
        return self.__sid

    def timeout(self):
        return self.__timeout

    def then_error(self, onError: Callable[[Exception], None]):
        self.__onError = onError
