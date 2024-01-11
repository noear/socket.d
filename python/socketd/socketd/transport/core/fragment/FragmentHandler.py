from typing import Callable

from socketd.transport.core import Entity
from socketd.transport.core.Message import Message
from socketd.transport.core.stream.StreamManger import StreamInternal


class FragmentHandler:
    """
    数据分片处理
    """

    async def split_fragment(self, channel: 'Channel', stream: StreamInternal,
                       message: Message, consumer: Callable[[Entity], None]):
        """拆割分片"""
        ...

    def aggrFragment(self, channel: 'Channel', fragmentIndex, frame):
        """
        聚合所有分片
        """
        ...

    def aggrEnable(self) -> bool:
        """聚合启用"""
        ...
