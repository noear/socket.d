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
            eventB: bytes = frame.message().event().encode(self.config.get_charset())
            # metaString
            metaStringB: bytes = frame.message().entity().meta_string().encode(self.config.get_charset())

            # length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            len1 = 4 + 4 + len(sidB) + len(eventB) + len(metaStringB) + frame.message().data_size() + 2 * 3

            Asserts.assert_size("sid", len(sidB), Constants.MAX_SIZE_SID)
            Asserts.assert_size("event", len(eventB), Constants.MAX_SIZE_EVENT)
            Asserts.assert_size("metaString", len(metaStringB), Constants.MAX_SIZE_META_STRING)
            Asserts.assert_size("data", frame.message().entity().data_size(), Constants.MAX_SIZE_DATA)

            target: CodecWriter = writerFactory(len1)

            # length
            target.put_int(len1)

            # flag
            target.put_int(frame.flag())

            # sid
            target.put_bytes(sidB)
            target.put_char(10) #'\n'

            # event
            target.put_bytes(eventB)
            target.put_char(10)

            # metaString
            target.put_bytes(metaStringB)
            target.put_char(10)

            # _data
            if frame.message().entity().data() is not None:
                target.put_bytes(frame.message().entity().data().getvalue())
            target.flush()

            return target

    def read(self, _reader: CodecReader) -> Frame | None:
        frameSize = _reader.get_int()

        if frameSize > (_reader.remaining() + 4):
            return None

        flag = _reader.get_int()  # 取前一位数据

        if frameSize == 8:
            # len + flag
            return Frame(Flags.of(flag), None)
        else:
            metaBufSize = min(Constants.MAX_SIZE_META_STRING, _reader.remaining())

            # 1. decode sid and event
            buf = Buffer(limit=metaBufSize)

            sid = self.decodeString(_reader, buf, Constants.MAX_SIZE_SID)

            event = self.decodeString(_reader, buf, Constants.MAX_SIZE_EVENT)

            metaString = self.decodeString(_reader, buf, Constants.MAX_SIZE_META_STRING)

            # 2. decode body
            dataRealSize = frameSize - _reader.position()
            data: Optional[bytearray] = None
            if dataRealSize > Constants.MAX_SIZE_DATA:
                # exceeded the limit, read and discard the bytes
                data = bytearray(Constants.MAX_SIZE_DATA)
                _reader.get_buffer().readinto(data)
                for i in range(dataRealSize - Constants.MAX_SIZE_DATA):
                    _reader.get_buffer().read()
            else:
                data = bytearray(_reader.get_buffer().read(dataRealSize))

            message = (MessageBuilder()
                       .flag(Flags.of(flag))
                       .sid(sid)
                       .event(event)
                       .entity(EntityDefault().data_set(data).meta_string_set(metaString))
                       .build())
            buf.close()
            _reader.close()
            return Frame(message.flag(), message)

    def decodeString(self, reader: CodecReader, buf: Buffer, maxLen: int) -> str:
        b = bytearray(reader.get_buffer().readline(maxLen).replace(b'\x00\n', b''))
        if buf.limit() < 1:
            return ""
        return b.decode(self.config.get_charset())
