import pickle

from socketd.core.module.Entity import Entity, EntityMetas
from socketd.core.module.EntityDefault import EntityDefault
from socketd.core.module.Frame import Frame
from .FragmentAggregatorDefault import FragmentAggregatorDefault

from .FragmentHandler import FragmentHandler
from ..Buffer import Buffer
from ..Channel import Channel
from ..module.Message import Message


class FragmentHandlerDefault(FragmentHandler):

    def nextFragment(self, channel: Channel, fragmentIndex: int, message: Message) -> Entity | None:
        fragmentBuf = Buffer()
        pickle.dump(message, fragmentBuf)
        fragmentBytes = fragmentBuf.getbuffer()
        if len(fragmentBytes) == 0:
            return None

        fragmentEntity = EntityDefault().set_data(fragmentBytes)
        if fragmentIndex == 1:
            fragmentEntity.set_meta_map(message.get_meta_map())
        fragmentEntity.put_meta(EntityMetas.META_DATA_FRAGMENT_IDX, str(fragmentIndex))
        return fragmentEntity

    def aggrFragment(self, channel: Channel, index: int, message: Message) -> Frame | None:
        aggregator = channel.get_attachment(message.get_sid())
        if aggregator is None:
            aggregator = FragmentAggregatorDefault(message)
            channel.set_attachment(message.get_sid(), aggregator)

        aggregator.add(index, message)

        if aggregator.getDataLength() > aggregator.getDataStreamSize():
            return None  # Length is not enough, wait for the next fragment package
        else:
            channel.set_attachment(message.get_sid(), None)
            return aggregator.get()  # Reset as a merged frame
