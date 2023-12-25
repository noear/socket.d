from socketd.core.module.EntityDefault import EntityDefault
from socketd.core.module.Frame import Frame
from socketd.core.module.Message import Message
from .FragmentAggregator import FragmentAggregator
from .FragmentHolder import FragmentHolder
from ..Buffer import Buffer
from ..module.Entity import EntityMetas

from ..module.MessageDefault import MessageDefault
from ...exception.SocketdExecption import SocketDException


class FragmentAggregatorDefault(FragmentAggregator):
    """
    分片聚合器
    """
    def __init__(self, frame: Message):
        self.__fragments: list[FragmentHolder] = []
        self.__main: Message = frame
        data_length: int = frame.get_meta(EntityMetas.META_DATA_LENGTH)
        if data_length is None or type(data_length) != int:
            raise SocketDException(f"Missing {EntityMetas.META_DATA_LENGTH} meta, event= {frame.get_event()}")
        self.__data_length = data_length
        self.__data_stream_size = 0

    def add(self, index: int, message: Message):
        self.__fragments.insert(index, FragmentHolder(index, message))
        self.__data_stream_size += message.get_data_size()

    def get_sid(self) -> str:
        return self.__main.get_sid()

    def get_data_length(self) -> int:
        return self.__data_length

    def get_data_stream_size(self) -> int:
        return self.__data_stream_size

    def get(self) -> Frame:
        self.__fragments.sort(key=lambda x: x.index)

        byte_buffer: Buffer = Buffer()

        for fragment in self.__fragments:
            byte_buffer.write(fragment.message.get_data().getvalue())

        return Frame(self.__main.get_flag(),
                     MessageDefault()
                     .set_flag(self.__main.get_flag())
                     .set_sid(self.__main.get_sid())
                     .set_entity(EntityDefault().set_meta_map(self.__main.get_entity().get_meta_map())
                                 .set_data(byte_buffer))
                     )
