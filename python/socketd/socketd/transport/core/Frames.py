from socketd.transport.core.Flags import Flags
from socketd.transport.core.EntityMetas import EntityMetas
from socketd import SocketD
from socketd.transport.core.Message import Message
from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.Frame import Frame
from socketd.transport.core.entity.MessageBuilder import MessageBuilder
from socketd.transport.core.entity.StringEntity import StringEntity


class Frames:

    @staticmethod
    def connectFrame(sid:str, url:str, metaMap: dict):
        entity = StringEntity(url)
        entity.meta_map_put(metaMap)
        entity.meta_put(EntityMetas.META_SOCKETD_VERSION, SocketD.version())

        message = MessageBuilder().sid(sid).event(url).entity(entity).build()

        return Frame(Flags.Connect, message)

    @staticmethod
    def connackFrame(connectMessage: Message):
        entity = EntityDefault()
        entity.meta_put(EntityMetas.META_SOCKETD_VERSION, SocketD.version())
        entity.data_set(connectMessage.entity().data())

        message = MessageBuilder().sid(connectMessage.sid()).event(connectMessage.event()).entity(entity).build()

        return Frame(Flags.Connack, message)

    @staticmethod
    def pingFrame():
        return Frame(Flags.Ping, None)

    @staticmethod
    def pongFrame():
        return Frame(Flags.Pong, None)

    @staticmethod
    def closeFrame():
        return Frame(Flags.Close, None)

    @staticmethod
    def alarmFrame(_from: Message, alarm: str):
        _messageBuilder = MessageBuilder()
        if _from:
            _messageBuilder.sid(_from.sid()).event(_from.event())
            _messageBuilder.entity(StringEntity(alarm).meta_string_set(_from.data_as_string()))
        else:
            _messageBuilder.entity(StringEntity(alarm))
        return Frame(Flags.Alarm, _messageBuilder.build())