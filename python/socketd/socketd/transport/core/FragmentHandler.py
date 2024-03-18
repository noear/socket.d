from abc import ABC, abstractmethod
from typing import Callable

from socketd.transport.core.Frame import Frame
from socketd.transport.core.Message import MessageInternal
from socketd.transport.stream.StreamManger import StreamInternal
from socketd.transport.core import Channel


class FragmentHandler(ABC):
    """
    数据分片处理
    """

    @abstractmethod
    async def split_fragment(self, channel: Channel, stream: StreamInternal,
                             message: MessageInternal, consumer: Callable):
        """拆割分片"""
        ...

    @abstractmethod
    def aggr_fragment(self, channel: Channel, fragmentIndex:int, message:MessageInternal)->Frame:
        """
        聚合所有分片
        """
        ...

    @abstractmethod
    def aggr_enable(self) -> bool:
        """聚合启用"""
        ...
