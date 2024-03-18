from abc import ABC, abstractmethod

from socketd.transport.core.Frame import Frame
from socketd.transport.core.Message import Message, MessageInternal


# 分片聚合器
class FragmentAggregator(ABC):
    @abstractmethod
    def get_sid(self) -> str:
        """
        获取sid

        :return: str, 返回sid字符串
        """
        ...

    @abstractmethod
    def get_data_stream_size(self) -> int:
        """
        获取数据流的大小

        :return: int, 数据流的大小
        """
        ...

    @abstractmethod
    def get_data_length(self) -> int:
        """
        获取数据的长度

        :return: int, 数据的长度
        """
        ...

    @abstractmethod
    def add(self, index: int, message: MessageInternal):
        """
        添加消息到指定索引位置

        Args:
            index (int): 指定的索引位置
            message (Message): 要添加的消息

        Returns:
            None
        """
        ...

    @abstractmethod
    def get(self) -> Frame: ...

    """
    获取一个Frame对象。

    Returns:
        Frame: 返回获取到的Frame对象。
    """
