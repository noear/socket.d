import asyncio
import socket
from asyncio import StreamReader

from socketd.transport.core import Config
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.Codec import CodecWriter
from socketd.transport.core.Frame import Frame
from socketd.transport.core.codec import bytes_to_int32
from socketd.transport.core.codec.Buffer import Buffer
from socketd.transport.core.codec.ByteBufferCodecReader import ByteBufferCodecReader
from socketd.transport.core.codec.ByteBufferCodecWriter import ByteBufferCodecWriter
from socketd.transport.core.impl.LogConfig import log
from socketd_aio_tcp.TCPStreamIO import TCPStreamIO


class TcpAIOChannelAssistant(ChannelAssistant):
    def __init__(self, config: Config, loop: asyncio.AbstractEventLoop):
        self.config = config
        self.loop = loop

    async def write(self, stream_io: TCPStreamIO, frame: Frame) -> None:
        writer: CodecWriter = self.config.get_codec().write(frame,
                                                            lambda size: ByteBufferCodecWriter(Buffer(limit=size)))
        if writer is not None:
            _data = writer.get_buffer().getvalue()
            _len = len(_data)
            stream_io.writer.write(_len.to_bytes(length=4, byteorder='big', signed=False))
            stream_io.writer.write(_data)
            # Flush the write buffer
            await stream_io.writer.drain()
            writer.close()

    def is_valid(self, stream_io: TCPStreamIO) -> bool:
        if isinstance(stream_io.server, asyncio.Transport):
            return not stream_io.server.is_closing()
        elif isinstance(stream_io.server, asyncio.Server):
            return stream_io.server.is_serving()

    async def close(self, stream_io: TCPStreamIO) -> None:
        stream_io.writer.close()
        stream_io.server.close()

    def get_remote_address(self, stream_io: TCPStreamIO) -> str:
        target: socket.socket = stream_io.writer.get_extra_info('socket')
        return target.getpeername()

    def get_local_address(self, stream_io: TCPStreamIO) -> str:
        target: socket.socket = stream_io.writer.get_extra_info('socket')
        return target.getsockname()

    async def read(self, reader: StreamReader) -> Frame | None:
        lenBt = await reader.read(4)
        if lenBt is None:
            return None

        _len = bytes_to_int32(lenBt)
        _buffer = await reader.read(_len)
        if _buffer is None:
            return None
        buffer = Buffer(len(_buffer), _buffer)
        return self.config.get_codec().read(ByteBufferCodecReader(buffer))
