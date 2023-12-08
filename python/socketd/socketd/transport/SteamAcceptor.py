from abc import ABC

from socketd.core.module.Message import Message


class StreamAcceptor(ABC):
    """流接收器"""

    def is_single(self): ...

    """是否单发接收"""

    def is_done(self): ...

    """是否结束接收"""

    def timeout(self): ...

    """超时设定（单位：毫秒）"""

    def accept(self, message: Message, onError) -> None: ...

    """"接收答复流"""
