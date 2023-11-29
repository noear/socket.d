import pickle

from io import BytesIO

from socketd.core.config.Config import Config
from socketd.core.module.Entity import Entity, EntityMetas
from socketd.core.module.EntityDefault import EntityDefault
from socketd.core.module.Frame import Frame
from socketd.core.module.Message import Message

from .FragmentHandler import FragmentHandler
from ..module.MessageDefault import MessageDefault


class FragmentHandlerDefault(FragmentHandler):
    def __init__(self):
        pass

    def nextFragment(self, config: Config, fragmentIndex: int, entity: Entity) -> Entity:
        # fragmentIndex.set(fragmentIndex.get() + 1)

        fragmentBuf = BytesIO()
        # IoUtils.transferTo(entity.getData(), fragmentBuf, 0, Config.MAX_SIZE_FRAGMENT)
        pickle.dump(entity, fragmentBuf)
        fragmentBytes = fragmentBuf.getbuffer()
        if len(fragmentBytes) == 0:
            return None
        fragmentEntity = EntityDefault().data(fragmentBytes)
        if fragmentIndex == 1:
            fragmentEntity.metaMap(entity.get_meta_map())
        fragmentEntity.putMeta(EntityMetas.META_DATA_FRAGMENT_IDX, str(fragmentIndex))
        return fragmentEntity

    def aggrFragment(self, channel, index: int, frame: Frame) -> Frame:
        aggregator = channel.getAttachment(frame.get_message().get_sid())
        if aggregator is None:
            aggregator = FragmentAggregator(frame)
            channel.setAttachment(frame.get_message().get_sid(), aggregator)

        aggregator.add(index, frame)

        if aggregator.getDataLength() > aggregator.getDataStreamSize():
            return None  # Length is not enough, wait for the next fragment package
        else:
            return aggregator.get()  # Reset as a merged frame


class FragmentAggregator():
    def __init__(self, frame: Frame):
        self.fragments = []
        self.sid = frame.get_message().get_sid()
        self.main: Frame = frame
        self.message: Message = frame.get_message()

    def add(self, index: int, frame: Frame):
        self.fragments.append((index, frame))

    def getDataLength(self) -> int:
        length = 0
        for fragment in self.fragments:
            length += len(fragment[1].getEntity().getData())
        return length

    def getDataStreamSize(self) -> int:
        if len(self.fragments) == 0:
            return 0
        else:
            return len(self.fragments[0][1].getEntity().getData())

    def get(self) -> Frame:
        length = self.getDataLength()
        entityBytes = bytearray(length)

        for fragment in self.fragments:
            entity = fragment[1].getEntity()
            data = entity.getData()
            index = fragment[0]
            size = len(data)
            start = index * size
            end = start + size
            entityBytes[start:end] = data

        return Frame(self.main.get_flag(),
                     MessageDefault()
                     .set_flag(self.main.flag)
                     .set_sid(self.message.get_sid())
                     .set_entity(EntityDefault().set_metaMap(self.main.get_message().get_entity().get_meta_map())
                                 .set_data(entityBytes))
                     )
