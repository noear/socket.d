import asyncio
from abc import ABC
from typing import Callable, Optional

from socketd.exception.SocketDExecption import SocketDTimeoutException
from socketd.transport.core.Message import MessageInternal
from socketd.transport.stream.Stream import StreamInternal
from socketd.transport.stream.StreamManger import StreamManger
from socketd.utils.CompletableFuture import CompletableFuture
from socketd.utils.LogConfig import log
from socketd.utils.RunUtils import RunUtils


class StreamBase(StreamInternal, ABC):
    """流接收器基类
    :param timeout: 超时（毫秒）
    """
    def __init__(self, sid: str, demands: int, timeout: float):
        self.__sid:str = sid
        self.__timeout:float = timeout
        self.__demands:int = demands

        self.__doOnError: Callable[[Exception], None] = None
        self.__doOnProgress: Callable[[bool, int, int], None] = None

        self.__insuranceFuture: Optional[asyncio.Future] = None

    def sid(self) -> str:
        return self.__sid

    def is_done(self):
        return True

    def _then_error_do(self, onError: Callable[[Exception], None]):
        self.__doOnError = onError

    def _then_progress_do(self, onProgress: Callable[[bool, int, int], None]):
        self.__doOnProgress = onProgress

    def demands(self) -> int:
        return self.__demands

    def timeout(self) ->float:
        return self.__timeout

    # 保险开始（避免永久没有回调，造成内存不能释放）
    def insurance_start(self, streamManger: StreamManger, streamTimeout: float) -> None:
        if self.__insuranceFuture:
            return

        async def __insuranceFuture():
            await asyncio.sleep(streamTimeout)
            streamManger.remove_stream(self.__sid)
            self.on_error(SocketDTimeoutException(f"The stream response timeout, sid={self.__sid}"))

        self.__insuranceFuture = CompletableFuture(__insuranceFuture())
        asyncio.run_coroutine_threadsafe(self.__insuranceFuture.get(streamTimeout), asyncio.get_running_loop())

    # 保险取消
    def insurance_cancel(self) -> None:
        if self.__insuranceFuture:
            self.__insuranceFuture.cancel()

    def on_reply(self, reply:MessageInternal):
        ...

    def on_error(self, error: Exception):
        if self.__doOnError:
            RunUtils.taskTry(self.__doOnError(error))
        else:
            log.debug(f"The stream error, sid={self.sid()}", error)

    def on_progress(self, is_send, val, max_val):
        if self.__doOnProgress:
            RunUtils.taskTry(self.__doOnProgress(is_send, val, max_val))



