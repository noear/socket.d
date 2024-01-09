from abc import ABC, abstractmethod
from io import BytesIO
from typing import Optional, Callable

from socketd.transport.core import Entity
from socketd.transport.core.Costants import EntityMetas
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Message import Message, MessageInternal
from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.fragment.FragmentAggregator import FragmentAggregator
from socketd.transport.core.fragment.FragmentAggregatorDefault import FragmentAggregatorDefault
from socketd.transport.core.fragment.FragmentHandler import FragmentHandler
from socketd.transport.core.stream.StreamManger import StreamInternal


class FragmentHandlerBase(FragmentHandler, ABC):

    def split_fragment(self, channel: Channel, stream: StreamInternal, message: Message,
                       consumer: Callable[[Entity], None]):
        if message.get_data_size() > channel.get_config().get_fragment_size():
            fragment_total = message.get_data_size()  # channel.get_config().fragment_size
            if message.get_data_size() % channel.get_config().get_fragment_size() > 0:
                fragment_total += 1

            fragment_index = 0
            while True:
                # Generate fragment
                fragment_index += 1
                data_buffer = FragmentHandlerBase.__read_fragment_data(message.get_data(),
                                                                       channel.get_config().get_fragment_size())
                if data_buffer is None or len(data_buffer.getbuffer()) == 0:
                    return

                fragment_entity = EntityDefault().set_data(data_buffer)
                if fragment_index == 1:
                    fragment_entity.meta_map_put(message.get_meta_map())
                fragment_entity.meta_put(EntityMetas.META_DATA_FRAGMENT_IDX, str(fragment_index))
                fragment_entity.meta_put(EntityMetas.META_DATA_FRAGMENT_TOTAL, str(fragment_total))

                consumer(fragment_entity)
                if stream is not None:
                    stream.on_progress(True, fragment_index, fragment_total)

        else:
            consumer(message)
            if stream is not None:
                stream.on_progress(True, 1, 1)

    def aggrFragment(self, channel: Channel, index: int, message: MessageInternal) -> Optional[Frame]:
        aggregator = channel.get_attachment(message.get_sid())
        if aggregator is None:
            aggregator = FragmentAggregatorDefault(message)
            channel.set_attachment(message.get_sid(), aggregator)

        aggregator.add(index, message)

        if aggregator.get_data_length() > aggregator.get_data_stream_size():
            return None  # Length is not enough, wait for the next fragment package
        else:
            channel.set_attachment(message.get_sid(), None)
            return aggregator.get()  # Reset as a merged frame

    @abstractmethod
    def createFragmentAggregator(self, message: MessageInternal) -> FragmentAggregator:
        """创建分片聚合器"""
        ...

    @staticmethod
    def __read_fragment_data(ins: BytesIO, max_size: int) -> BytesIO:
        size = min(len(ins.getbuffer()) - ins.tell(), max_size)
        buf = BytesIO()
        buf.write(ins.read(size))
        return buf
