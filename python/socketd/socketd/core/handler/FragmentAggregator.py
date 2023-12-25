from socketd.core.module.Frame import Frame
from socketd.core.module.Message import Message


class FragmentAggregator:

    def get_sid(self) -> str:
        """
        获取sid

        :return: str, 返回sid字符串
        """
        ...

    def get_data_stream_size(self) -> int:
        """
        获取数据流的大小

        :return: int, 数据流的大小
        """
        ...

    def get_data_length(self) -> int:
        """
        获取数据的长度

        :return: int, 数据的长度
        """
        ...

    def add(self, index: int, message: Message):
        '''
        添加消息到指定索引位置

        Args:
            index (int): 指定的索引位置
            message (Message): 要添加的消息

        Returns:
            None
        '''
        ...

    def get(self) -> Frame: ...

    """
    获取一个Frame对象。

    Returns:
        Frame: 返回获取到的Frame对象。
    """
