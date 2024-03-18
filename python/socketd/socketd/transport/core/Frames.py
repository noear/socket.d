from socketd import SocketD
from socketd.transport.core.Flags import Flags
from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.HandshakeDefault import HandshakeInternal
from socketd.transport.core.Message import Message
from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.Frame import Frame
from socketd.transport.core.entity.MessageBuilder import MessageBuilder
from socketd.transport.core.entity.StringEntity import StringEntity


class Frames:

    @staticmethod
    def connect_frame(sid: str, url: str, metaMap: dict[str, str]) -> Frame:
        entity = StringEntity(url)
        # 添加框架版本号
        entity.meta_map_put(metaMap)
        entity.meta_put(EntityMetas.META_SOCKETD_VERSION, SocketD.version())

        message = MessageBuilder().sid(sid).event(url).entity(entity).build()

        return Frame(Flags.Connect, message)

    @staticmethod
    def connack_frame(handshake: HandshakeInternal):
        entity = EntityDefault()
        # 添加框架版本号
        entity.meta_map_put(handshake.get_out_meta_map())
        entity.meta_put(EntityMetas.META_SOCKETD_VERSION, SocketD.version())
        entity.data_set(handshake.get_source().entity().data())

        message = (MessageBuilder().sid(handshake.get_source().sid())
                   .event(handshake.get_source().event())
                   .entity(entity).build())

        return Frame(Flags.Connack, message)

    @staticmethod
    def ping_frame():
        return Frame(Flags.Ping, None)

    @staticmethod
    def pong_frame():
        return Frame(Flags.Pong, None)

    @staticmethod
    def close_frame(_code: int):
        messageBuilder = MessageBuilder()
        messageBuilder.entity(EntityDefault().meta_put("code", str(_code)))

        return Frame(Flags.Close, messageBuilder.build())

    @staticmethod
    def alarm_frame(_from: Message, alarm: str):
        messageBuilder = MessageBuilder()

        if _from:
            messageBuilder.sid(_from.sid()).event(_from.event())
            messageBuilder.entity(StringEntity(alarm).meta_string_set(_from.data_as_string()))
        else:
            messageBuilder.entity(StringEntity(alarm))
        return Frame(Flags.Alarm, messageBuilder.build())
