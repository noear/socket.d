import asyncio
import socket

from socketd.transport.core import Config
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.Codec import CodecWriter
from socketd.transport.core.Frame import Frame
from socketd.transport.core.codec import bytes_to_int32
from socketd.transport.core.codec.Buffer import Buffer
from socketd.transport.core.codec.CodecByteBuffer import ByteBufferCodecWriter, ByteBufferCodecReader


class TcpAIOChannelAssistant(ChannelAssistant):
    def __init__(self, config: Config, loop: asyncio.AbstractEventLoop):
        self.config = config
        self.loop = loop

    async def write(self, source: socket.socket, frame: Frame) -> None:
        writer: CodecWriter = self.config.get_codec().write(frame,
                                                            lambda size: ByteBufferCodecWriter(Buffer(limit=size)))
        if writer is not None:
            _data = writer.get_buffer().getvalue()
            _len = len(_data)
            await self.loop.sock_sendall(source, _len.to_bytes(length=4, byteorder='big', signed=False))
            await self.loop.sock_sendall(source, _data)
            writer.close()

    def is_valid(self, target: socket.socket) -> bool:
        return not getattr(target, '_closed')

    async def close(self, target: socket.socket) -> None:
        target.close()

    def get_remote_address(self, target: socket.socket) -> str:
        return target.getpeername()

    def get_local_address(self, target: socket.socket) -> str:
        return target.getsockname()

    async def read(self, sock: socket.socket) -> Frame | None:
        loop = asyncio.get_running_loop()
        lenBt = await loop.sock_recv(sock, 4)
        if lenBt is None:
            return None

        _len = bytes_to_int32(lenBt)
        _buffer = await loop.sock_recv(sock, _len)
        if _buffer is None:
            return None
        buffer = Buffer(len(_buffer), _buffer)
        return self.config.get_codec().read(ByteBufferCodecReader(buffer))
