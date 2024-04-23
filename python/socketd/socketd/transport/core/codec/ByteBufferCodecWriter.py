from io import BytesIO

from socketd.transport.core.codec.Buffer import Buffer
from socketd.transport.core.Codec import CodecWriter

class ByteBufferCodecWriter(CodecWriter):

    def __init__(self, buffer: Buffer):
        self.__buffer = buffer

    def put_bytes(self, _bytes: bytearray | memoryview | bytes):
        self.__buffer.write(_bytes)

    def put_int(self, _num: int):
        self.__buffer.put_int(_num)

    def put_char(self, _char: int):
        self.__buffer.put_char(_char)

    def flush(self):
        self.__buffer.flush()

    def get_buffer(self) -> BytesIO:
        return self.__buffer

    def close(self):
        self.__buffer.close()
