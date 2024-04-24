from abc import ABC, abstractmethod
from io import BytesIO
from typing import Optional, Callable

from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Message import Message, MessageInternal
from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.FragmentAggregator import FragmentAggregator
from socketd.transport.core.fragment.FragmentAggregatorDefault import FragmentAggregatorDefault
from socketd.transport.core.FragmentHandler import FragmentHandler
from socketd.transport.stream.Stream import StreamInternal


class FragmentHandlerBase(FragmentHandler, ABC):

    async def split_fragment(self, channel: Channel, stream: StreamInternal, message: MessageInternal, consumer: Callable):
        if message.data_size() > channel.get_config().get_fragment_size():
            fragment_total = message.data_size()  # channel.get_config().fragment_size
            if message.data_size() % channel.get_config().get_fragment_size() > 0:
                fragment_total += 1

            fragment_index = 0
            while True:
                # Generate fragment
                fragment_index += 1
                data_buffer = FragmentHandlerBase.__read_fragment_data(message.data(), message.data_size(),
                                                                       channel.get_config().get_fragment_size())
                if data_buffer is None or len(data_buffer.getbuffer()) == 0:
                    return

                fragment_entity: EntityDefault = EntityDefault().data_set(data_buffer)
                if fragment_index == 1:
                    fragment_entity.meta_map_put(message.meta_map())
                fragment_entity.meta_put(EntityMetas.META_DATA_FRAGMENT_IDX, str(fragment_index))
                fragment_entity.meta_put(EntityMetas.META_DATA_FRAGMENT_TOTAL, str(fragment_total))

                await consumer(fragment_entity)
                if stream is not None:
                    stream.on_progress(True, fragment_index, fragment_total)
                data_buffer.close()

        else:
            await consumer(message)
            if stream is not None:
                stream.on_progress(True, 1, 1)

    def aggr_fragment(self, channel: Channel, index: int, message: MessageInternal) -> Optional[Frame]:
        aggregator = channel.get_attachment(message.sid())
        if aggregator is None:
            aggregator = FragmentAggregatorDefault(message)
            channel.put_attachment(message.sid(), aggregator)

        aggregator.add(index, message)

        if aggregator.get_data_length() > aggregator.get_data_stream_size():
            return None  # Length is not enough, wait for the next fragment package
        else:
            channel.put_attachment(message.sid(), None)
            return aggregator.get()  # Reset as a merged frame

    @abstractmethod
    def create_fragment_aggregator(self, message: MessageInternal) -> FragmentAggregator:
        """创建分片聚合器"""
        ...

    @staticmethod
    def __read_fragment_data(ins: BytesIO, ins_size: int, max_size: int) -> BytesIO:
        size = min(ins_size - ins.tell(), max_size)
        buf = BytesIO()
        buf.write(ins.read(size))
        return buf
