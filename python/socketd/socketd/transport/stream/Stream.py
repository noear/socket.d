from abc import ABC, abstractmethod
from typing import Callable


class Stream(ABC):

    @abstractmethod
    def get_sid(self) -> str:
        """流ID"""
        ...

    @abstractmethod
    def is_done(self):
        """是否结束接收"""
        ...

    @abstractmethod
    def timeout(self):
        """超时设定（单位：毫秒）"""
        ...

    @abstractmethod
    def then_error(self, onError: Callable[[Exception], None]):
        """
        异常发生

        :param onError: 当异常发生时执行的函数，接受一个异常参数
        """
        ...

    @abstractmethod
    def then_progress(self, on_progress: Callable[[bool, int, int], None]): ...
