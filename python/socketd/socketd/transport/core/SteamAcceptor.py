from abc import ABC
from typing import Callable

from socketd.core.module.Message import Message


class Steam(ABC):

    def get_sid(self) -> str:
        """流ID"""
        ...

    def is_single(self):
        """是否单发接收"""
        ...

    def is_done(self):
        """是否结束接收"""
        ...

    def timeout(self):
        """超时设定（单位：毫秒）"""
        ...

    def then_error(self, onError: Callable[[Exception], None]):
        """
        异常发生

        :param onError: 当异常发生时执行的函数，接受一个异常参数
        """
        ...


class StreamInternal(Steam):
    """流接收器"""

    def on_accept(self, message: Message, onError) -> None:
        """
        接收答复流

        Args:
            message (Message): 消息对象
            onError: 错误处理函数

        Returns:
            None
        """
        ...

    def on_error(self, error: Exception):
        """异常时"""
        ...
