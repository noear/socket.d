from io import BytesIO

from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Message import MessageInternal
from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.FragmentAggregator import FragmentAggregator
from socketd.exception.SocketDExecption import SocketDException

from .FragmentHolder import FragmentHolder
from ..entity.MessageBuilder import MessageBuilder
from ...utils.StrUtil import StrUtil


class FragmentAggregatorDefault(FragmentAggregator):
    """
    分片聚合器
    """

    def __init__(self, frame: MessageInternal):
        self.__main: MessageInternal = frame
        self.__fragmentHolders: list[FragmentHolder] = []

        data_length: str = frame.meta(EntityMetas.META_DATA_LENGTH)
        if StrUtil.is_empty(data_length):
            raise SocketDException(f"Missing {EntityMetas.META_DATA_LENGTH} meta, event= {frame.event()}")
        self.__data_length = int(data_length)
        self.__data_stream_size = 0

    def add(self, index: int, message: MessageInternal):
        self.__fragmentHolders.insert(index, FragmentHolder(index, message))
        self.__data_stream_size += message.data_size()

    def get_sid(self) -> str:
        return self.__main.sid()

    def get_data_length(self) -> int:
        return self.__data_length

    def get_data_stream_size(self) -> int:
        return self.__data_stream_size

    def get(self) -> Frame:
        self.__fragmentHolders.sort(key=lambda x: x.index)

        byte: BytesIO = BytesIO()

        for fragment in self.__fragmentHolders:
            byte.write(fragment.message.data().getvalue())

        return Frame(self.__main.flag(),
                     MessageBuilder()
                     .flag(self.__main.flag())
                     .sid(self.__main.sid())
                     .entity(EntityDefault().meta_map_put(self.__main.entity().meta_map())).build()
                     )
