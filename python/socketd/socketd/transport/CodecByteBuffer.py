from typing import Callable
from .Codec import Codec
from socketd.core.module.Frame import Frame
from socketd.core.module.MessageDefault import MessageDefault
from socketd.core.module.EntityDefault import EntityDefault
from socketd.core.Costants import Flag
from socketd.core.config.Config import Config
from ..core.Buffer import Buffer


class CodecByteBuffer(Codec):
    def __init__(self, config: 'ConfigBase'):
        self.config = config

    def write(self, frame: Frame, factory: Callable[[int], Buffer]) -> Buffer:
        if frame.message is None:
            # length (flag + int.bytes)
            _len = 2 * 4
            target = factory(_len)

            # length
            target.put_int(_len)
            # flag
            target.put_int(frame.flag)
            target.flush()

            return target
        else:
            # sid
            sidB = frame.message.get_sid().encode(self.config.get_charset())
            # event
            topicB = frame.message.get_event().encode(self.config.get_charset())
            # metaString
            metaStringB = frame.message.get_entity().get_meta_string().encode(self.config.get_charset())

            # length (flag + sid + event + metaString + data + int.bytes + \n*3)
            len1 = len(sidB) + len(topicB) + len(
                metaStringB) + frame.message.get_entity().get_data_size() + 1 * 3 + 2 * 4

            self.assertSize("sid", len(sidB), Config.MAX_SIZE_SID)
            self.assertSize("event", len(topicB), Config.MAX_SIZE_TOPIC)
            self.assertSize("metaString", len(metaStringB), Config.MAX_SIZE_META_STRING)
            self.assertSize("data", frame.message.get_entity().get_data_size(), Config.MAX_SIZE_FRAGMENT)

            target: Buffer = factory(len1)

            # length
            target.put_int(len1)

            # flag
            target.put_int(frame.flag)

            # sid
            target.write(sidB)
            target.write(b'\n')

            # event
            target.write(topicB)
            target.write(b'\n')

            # metaString
            target.write(metaStringB)
            target.write(b'\n')

            # data
            if frame.message.get_entity().get_data() is not None:
                target.write(frame.message.get_entity().get_data().getvalue())
            target.flush()

            return target

    def read(self, buffer: Buffer) -> Frame:
        len0 = buffer.get_int()

        if len0 > (buffer.remaining() + 4):
            return None

        flag = buffer.get_int()  # 取前一位数据

        if len0 == 8:
            # len + flag
            return Frame(Flag.of(flag), None)
        else:
            metaBufSize = min(Config.MAX_SIZE_META_STRING, buffer.remaining())
            # 1. decode sid and event
            by = Buffer(limit=metaBufSize)
            sid = self.decodeString(buffer, by, Config.MAX_SIZE_SID)
            topic = self.decodeString(buffer, by, Config.MAX_SIZE_TOPIC)
            metaString = self.decodeString(buffer, by, Config.MAX_SIZE_META_STRING)

            # 2. decode body
            dataRealSize = len0 - buffer.tell()
            data: bytearray = None
            if dataRealSize > Config.MAX_SIZE_FRAGMENT:
                # exceeded the limit, read and discard the bytes
                data = bytearray(Config.MAX_SIZE_FRAGMENT)
                buffer.readinto(data)
                for i in range(dataRealSize - Config.MAX_SIZE_FRAGMENT):
                    buffer.read()
            else:
                data = bytearray(buffer.read(dataRealSize))

            message = MessageDefault().set_sid(sid).set_event(topic).set_entity(
                EntityDefault().set_meta_string(metaString).set_data(data)
            )
            message.flag = Flag.of(flag)
            return Frame(message.flag, message)

    def decodeString(self, reader: Buffer, buf: Buffer, maxLen: int) -> str:
        buf.seek(0)

        while True:
            c = reader.read(1)

            if c == b'\n':
                break

            if 0 < maxLen <= buf.tell():
                # exceeded the limit, read and discard the bytes
                pass
            else:
                if c != b' ':
                    buf.write(c)

        # buf.flip()
        if buf.limit() < 1:
            return ""

        return buf.getvalue().decode(self.config.get_charset())

    def assertSize(self, name: str, size: int, limitSize: int) -> None:
        if size > limitSize:
            buf = f"This message {name} size is out of limit {limitSize} ({size})"
            raise RuntimeError(buf)
