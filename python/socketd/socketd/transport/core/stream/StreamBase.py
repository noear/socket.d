import asyncio
from abc import ABC
from typing import Callable, Optional

from socketd.exception.SocketdTimeoutException import SocketdTimeoutException
from socketd.transport.core.stream.StreamManger import StreamInternal, StreamManger
from socketd.transport.utils.CompletableFuture import CompletableFuture


class StreamBase(StreamInternal, ABC):
    """流接收器基类"""

    def __init__(self, sid: str, demands: int, timeout: int):
        self.__sid = sid
        self.__timeout = timeout
        self.__demands = demands
        self.__doOnError: Callable[[Exception], None] = None
        self.__doOnProgress: Callable[[bool, int, int], None] = None
        self.__insuranceFuture: Optional[asyncio.Future] = None

    def on_error(self, error: Exception):
        if error:
            self.__doOnError(error)

    def is_done(self):
        return True

    def then_progress(self, on_progress: Callable[[bool, int, int], None]):
        pass

    def get_sid(self) -> str:
        return self.__sid

    def timeout(self):
        return self.__timeout

    def then_error(self, onError: Callable[[Exception], None]):
        self.__onError = onError

    def demands(self) -> int:
        return self.__demands

    def insurance_start(self, streamManger: StreamManger, streamTimeout: float) -> None:
        if self.__insuranceFuture:
            return

        async def __insuranceFuture():
            await asyncio.sleep(streamTimeout)
            streamManger.remove_stream(self.__sid)
            self.on_error(SocketdTimeoutException(f"The stream response timeout, sid={self.__sid}"))

        self.__insuranceFuture = CompletableFuture(__insuranceFuture())
        asyncio.run_coroutine_threadsafe(self.__insuranceFuture.get(streamTimeout), asyncio.get_running_loop())

    def insurance_cancel(self) -> None:
        if self.__insuranceFuture:
            self.__insuranceFuture.cancel()

    def on_progress(self, is_send, val, max_val):
        if self.__doOnProgress:
            self.__doOnProgress(is_send, val, max_val)
