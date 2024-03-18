from io import BytesIO

from socketd.transport.core.codec.Buffer import Buffer
from socketd.transport.core.Codec import CodecReader


class ByteBufferCodecReader(CodecReader):

    def __init__(self, buffer: Buffer):
        self.__buffer = buffer

    def get_bytes(self) -> bytes:
        return self.__buffer.getvalue()

    def get_int(self) -> int:
        return int.from_bytes(self.__buffer.read1(4), byteorder='little', signed=False)

    def skip_bytes(self, size):
        self.__buffer.seek(self.__buffer.tell() + size)

    def remaining(self):
        return self.__buffer.remaining()

    def position(self):
        return self.__buffer.tell()

    def get_buffer(self) -> BytesIO:
        return self.__buffer

    def close(self):
        self.__buffer.close()


