from io import BytesIO
from typing import Callable, Optional
from socketd.transport.core.Costants import Constants, Flag
from socketd.transport.core.Frame import Frame
from socketd.transport.core.entity.MessageDefault import MessageDefault
from socketd.transport.core.entity.EntityDefault import EntityDefault
from socketd.transport.core.Config import Config
from socketd.transport.core.codec.Buffer import Buffer
from socketd.transport.core.Codec import Codec, CodecReader, CodecWriter


def assert_size(name: str, size: int, limitSize: int) -> None:
    if size > limitSize:
        buf = f"This message {name} size is out of limit {limitSize} ({size})"
        raise RuntimeError(buf)


class ByteBufferCodecReader(CodecReader):

    def __init__(self, buffer: Buffer):
        self.__buffer = buffer

    def get_bytes(self, size) -> bytes:
        return self.__buffer.read(size)

    def get_int(self) -> int:
        return int.from_bytes(self.__buffer.read1(4), byteorder='big', signed=False)

    def skip_bytes(self, size):
        self.__buffer.seek(self.__buffer.tell() + size)

    def seek(self, size):
        self.__buffer.seek(size)

    def remaining(self):
        return self.__buffer.remaining()

    def position(self):
        return self.__buffer.tell()

    def get_buffer(self) -> BytesIO:
        return self.__buffer

    def close(self):
        self.__buffer.close()


class ByteBufferCodecWriter(CodecWriter):

    def __init__(self, buffer: Buffer):
        self.__buffer = buffer

    def put_bytes(self, _bytes: bytearray | memoryview | bytes):
        self.__buffer.write(_bytes)

    def put_int(self, _num: int):
        self.__buffer.write(_num.to_bytes(length=4, byteorder='big', signed=False))

    def put_char(self, _val: bytes):
        by: int = int.from_bytes(_val, byteorder='big', signed=False)
        self.__buffer.write(by.to_bytes(length=2,byteorder='big', signed=False))
        # self.__buffer.write(_val)

    def flush(self):
        self.__buffer.flush()

    def get_buffer(self) -> Buffer:
        return self.__buffer

    def close(self):
        self.__buffer.close()


class CodecByteBuffer(Codec):
    def __init__(self, config: Config):
        self.config = config

    def write(self, frame: Frame, factory: Callable[[int], CodecWriter]) -> CodecWriter:
        if frame.message is None:
            # length (flag + int.bytes)
            _len = 2 * 4
            target: CodecWriter = factory(_len)
            # length
            target.put_int(_len)
            # flag
            target.put_int(frame.flag)
            target.flush()

            return target
        else:
            # sid
            sidB: bytes = frame.message.get_sid().encode(self.config.get_charset())
            # event
            event: bytes = frame.message.get_event().encode(self.config.get_charset())
            # metaString
            metaStringB: bytes = frame.message.get_entity().get_meta_string().encode(self.config.get_charset())

            # length (flag + sid + event + metaString + data + int.bytes + \n*3)
            len1 = len(sidB) + len(event) + len(
                metaStringB) + frame.message.get_entity().get_data_size() + 1 * 3 + 2 * 4

            assert_size("sid", len(sidB), Constants.MAX_SIZE_SID)
            assert_size("event", len(event), Constants.MAX_SIZE_EVENT)
            assert_size("metaString", len(metaStringB), Constants.MAX_SIZE_META_STRING)
            assert_size("data", frame.message.get_entity().get_data_size(), Constants.MAX_SIZE_FRAGMENT)

            target: CodecWriter = factory(len1)

            # length
            target.put_int(len1)

            # flag
            target.put_int(frame.flag)

            # sid
            target.put_bytes(sidB)
            target.put_char(b'\n')

            # event
            target.put_bytes(event)
            target.put_char(b'\n')

            # metaString
            target.put_bytes(metaStringB)
            target.put_char(b'\n')

            # _data
            if frame.message.get_entity().get_data() is not None:
                target.put_bytes(frame.message.get_entity().get_data().getvalue())
            target.flush()

            return target

    def read(self, _reader: CodecReader) -> Frame | None:
        len0 = _reader.get_int()

        if len0 > (_reader.remaining() + 4):
            return None
        flag = _reader.get_int()  # 取前一位数据

        if len0 == 8:
            # len + flag
            return Frame(Flag.of(flag), None)
        else:
            metaBufSize = min(Constants.MAX_SIZE_META_STRING, _reader.remaining())
            # 1. decode sid and event
            by = Buffer(limit=metaBufSize)
            sid = self.decodeString(_reader, by, Constants.MAX_SIZE_SID)
            event = self.decodeString(_reader, by, Constants.MAX_SIZE_EVENT)
            metaString = self.decodeString(_reader, by, Constants.MAX_SIZE_META_STRING)

            # 2. decode body
            dataRealSize = len0 - _reader.position() + 3
            data: Optional[bytearray] = None
            if dataRealSize > Constants.MAX_SIZE_FRAGMENT:
                # exceeded the limit, read and discard the bytes
                data = bytearray(Constants.MAX_SIZE_FRAGMENT)
                _reader.get_buffer().readinto(data)
                for i in range(dataRealSize - Constants.MAX_SIZE_FRAGMENT):
                    _reader.get_bytes(1)
            else:
                data = bytearray(_reader.get_bytes(dataRealSize))

            message = MessageDefault().set_sid(sid).set_event(event).set_entity(
                EntityDefault().meta_string_set(metaString).data_set(data)
            )
            message.flag = Flag.of(flag)
            _reader.close()
            return Frame(message.flag, message)

    def decodeString(self, reader: CodecReader, buf: Buffer, maxLen: int) -> str:
        b = bytearray(reader.get_buffer().readline(maxLen))[:-2]
        if buf.limit() < 1:
            return ""
        return b.decode(self.config.get_charset())
