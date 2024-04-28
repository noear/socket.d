from abc import abstractmethod
from typing import Callable
from socketd.transport.stream.Stream import Stream
from socketd.transport.core.Entity import Reply


class RequestStream(Stream):
    @abstractmethod
    async def waiter(self) -> Reply:
        ...

    @abstractmethod
    def then_error(self, onError: Callable[[Exception], None]) -> 'RequestStream':
        """
        异常发生
        :param onError: 当异常发生时执行的函数，接受一个异常参数
        """
        ...

    @abstractmethod
    def then_progress(self, onProgress: Callable[[bool, int, int], None]) -> 'RequestStream':
        """
        进度发生时
        :param onProgress (isSend, val, max)
        """
        ...

    @abstractmethod
    def then_reply(self, onReply: Callable[[Reply], None]) -> 'RequestStream':
        """
        答复发生时
        """
        ...

