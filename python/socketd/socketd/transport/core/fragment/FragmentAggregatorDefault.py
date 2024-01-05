from io import BytesIO

from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Message import Message
from socketd.transport.core.Costants import EntityMetas
from socketd.transport.core.FragmentAggregator import FragmentAggregator
from socketd.transport.core.entity.MessageDefault import MessageDefault
from socketd.exception.SocketdExecption import SocketDException

from .FragmentHolder import FragmentHolder


class FragmentAggregatorDefault(FragmentAggregator):
    """
    分片聚合器
    """

    def __init__(self, frame: Message):
        self.__fragments: list[FragmentHolder] = []
        self.__main: Message = frame
        data_length: str = frame.get_meta(EntityMetas.META_DATA_LENGTH)
        if data_length is None or not data_length.isalnum():
            raise SocketDException(f"Missing {EntityMetas.META_DATA_LENGTH} meta, event= {frame.get_event()}")
        self.__data_length = int(data_length)
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

        byte: BytesIO = BytesIO()

        for fragment in self.__fragments:
            byte.write(fragment.message.get_data().getvalue())

        return Frame(self.__main.get_flag(),
                     MessageDefault()
                     .set_flag(self.__main.get_flag())
                     .set_sid(self.__main.get_sid())
                     .set_entity(EntityDefault().set_meta_map(self.__main.get_entity().get_meta_map())
                                 .set_data(byte))
                     )
