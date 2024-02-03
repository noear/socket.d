from socketd.transport.core.Costants import Flag, EntityMetas
from socketd import SocketD
from socketd.transport.core.Message import Message
from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.Frame import Frame
from socketd.transport.core.entity.MessageDefault import MessageDefault
from socketd.transport.core.entity.StringEntity import StringEntity


class Frames:

    @staticmethod
    def connectFrame(sid, url, metaMap: dict):
        entity = StringEntity(url)
        entity.meta_put_all(metaMap)
        entity.put_meta(EntityMetas.META_SOCKETD_VERSION, SocketD.version())
        return Frame(Flag.Connect, MessageDefault().set_sid(sid).set_event(url).set_entity(entity))

    @staticmethod
    def connackFrame(connectMessage: Message):
        entity = EntityDefault()
        entity.put_meta(EntityMetas.META_SOCKETD_VERSION, SocketD.version())
        entity.set_data(connectMessage.get_entity().get_data())
        return Frame(Flag.Connack, MessageDefault().set_sid(connectMessage.get_sid()).set_event(
            connectMessage.get_event()).set_entity(entity))

    @staticmethod
    def pingFrame():
        return Frame(Flag.Ping, None)

    @staticmethod
    def pongFrame():
        return Frame(Flag.Pong, None)

    @staticmethod
    def closeFrame():
        return Frame(Flag.Close, None)

    @staticmethod
    def alarmFrame(_from: Message, alarm: str):
        _message = MessageDefault()
        if _from:
            _message.set_sid(_from.get_sid()).set_event(_from.get_event())
            _message.set_entity(StringEntity(alarm).set_meta_string(_from.get_data_as_string()))
        else:
            _message.set_entity(StringEntity(alarm))
        return Frame(Flag.Alarm, _message)