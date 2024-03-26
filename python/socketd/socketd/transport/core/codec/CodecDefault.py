from typing import Callable, Optional

from socketd.transport.core.Asserts import Asserts
from socketd.transport.core.Codec import Codec, CodecReader, CodecWriter
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Frame import Frame
from socketd.transport.core.entity.MessageBuilder import MessageBuilder
from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.Config import Config
from socketd.transport.core.codec.Buffer import Buffer


class CodecDefault(Codec):
    def __init__(self, config: Config):
        self.config = config

    def write(self, frame: Frame, writerFactory: Callable[[int], CodecWriter]) -> CodecWriter:
        if frame.message() is None:
            # length (flag + int.bytes)
            _len = 2 * 4
            target: CodecWriter = writerFactory(_len)

            # length
            target.put_int(_len)
            # flag
            target.put_int(frame.flag())
            target.flush()

            return target
        else:
            # sid
            sidB: bytes = frame.message().sid().encode(self.config.get_charset())
            # event
            event: bytes = frame.message().event().encode(self.config.get_charset())
            # metaString
            metaStringB: bytes = frame.message().entity().meta_string().encode(self.config.get_charset())

            # length (flag + sid + event + metaString + data + int.bytes + \n*3)
            len1 = len(sidB) + len(event) + len(
                metaStringB) + frame.message().entity().data_size() + 1 * 3 + 2 * 4

            Asserts.assert_size("sid", len(sidB), Constants.MAX_SIZE_SID)
            Asserts.assert_size("event", len(event), Constants.MAX_SIZE_EVENT)
            Asserts.assert_size("metaString", len(metaStringB), Constants.MAX_SIZE_META_STRING)
            Asserts.assert_size("data", frame.message().entity().data_size(), Constants.MAX_SIZE_DATA)

            target: CodecWriter = writerFactory(len1)

            # length
            target.put_int(len1)

            # flag
            target.put_int(frame.flag())

            # sid
            target.put_bytes(sidB)
            target.put_bytes(b'\n')

            # event
            target.put_bytes(event)
            target.put_bytes(b'\n')

            # metaString
            target.put_bytes(metaStringB)
            target.put_bytes(b'\n')

            # _data
            if frame.message().entity().data() is not None:
                target.put_bytes(frame.message().entity().data().getvalue())
            target.flush()

            return target

    def read(self, _reader: CodecReader) -> Frame | None:
        len0 = _reader.get_int()

        if len0 > (_reader.remaining() + 4):
            return None

        flag = _reader.get_int()  # 取前一位数据

        if len0 == 8:
            # len + flag
            return Frame(Flags.of(flag), None)
        else:
            metaBufSize = min(Constants.MAX_SIZE_META_STRING, _reader.remaining())
            # 1. decode sid and event
            by = Buffer(limit=metaBufSize)
            sid = self.decodeString(_reader, by, Constants.MAX_SIZE_SID)
            event = self.decodeString(_reader, by, Constants.MAX_SIZE_EVENT)
            metaString = self.decodeString(_reader, by, Constants.MAX_SIZE_META_STRING)
            # 2. decode body
            dataRealSize = len0 - _reader.position()
            data: Optional[bytearray] = None
            if dataRealSize > Constants.MAX_SIZE_DATA:
                # exceeded the limit, read and discard the bytes
                data = bytearray(Constants.MAX_SIZE_DATA)
                _reader.get_buffer().readinto(data)
                for i in range(dataRealSize - Constants.MAX_SIZE_DATA):
                    _reader.get_buffer().read()
            else:
                data = bytearray(_reader.get_buffer().read(dataRealSize))

            message = MessageBuilder().flag(Flags.of(flag)).sid(sid).event(event).entity(
                EntityDefault().meta_string_set(metaString).data_set(data)
            ).build()
            by.close()
            _reader.close()
            return Frame(message.flag(), message)

    def decodeString(self, reader: CodecReader, buf: Buffer, maxLen: int) -> str:
        b = bytearray(reader.get_buffer().readline(maxLen).replace(b'\n', b''))
        if buf.limit() < 1:
            return ""
        return b.decode(self.config.get_charset())
