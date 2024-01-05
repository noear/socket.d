from io import BytesIO

from socketd.transport.core.Entity import Entity
from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.Frame import Frame
from socketd.transport.core.Channel import Channel
from socketd.transport.core.Message import Message
from socketd.transport.core.FragmentHandler import FragmentHandler
from socketd.transport.core.Costants import EntityMetas

from .FragmentAggregatorDefault import FragmentAggregatorDefault


class FragmentHandlerDefault(FragmentHandler):

    def nextFragment(self, channel: Channel, fragmentIndex: int, message: Message) -> Entity | None:
        data = self.read_frame(message.get_data(), channel.get_config().get_fragment_size())
        if len(data.getvalue()) == 0:
            return None
        fragmentEntity = EntityDefault().set_data(data)
        fragmentEntity.set_meta_map(message.get_meta_map())
        fragmentEntity.put_meta(EntityMetas.META_DATA_FRAGMENT_IDX, str(fragmentIndex))
        return fragmentEntity

    def aggrFragment(self, channel: Channel, index: int, message: Message) -> Frame | None:
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

    def read_frame(self, ins: BytesIO, max_size: int) -> BytesIO:
        size = min(len(ins.getbuffer()) - ins.tell(), max_size)
        buf = BytesIO()
        buf.write(ins.read(size))
        return buf
