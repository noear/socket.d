from abc import abstractmethod
from typing import Callable

from socketd.transport.stream.Stream import Stream


class SendStream(Stream):
    @abstractmethod
    def then_error(self, onError: Callable[[Exception], None]) -> 'SendStream':
        """
        异常发生
        :param onError: 当异常发生时执行的函数，接受一个异常参数
        """
        ...

    @abstractmethod
    def then_progress(self, onProgress: Callable[[bool, int, int], None]) -> 'SendStream':
        """
        进度发生时
        :param onProgress (isSend, val, max)
        """
        ...