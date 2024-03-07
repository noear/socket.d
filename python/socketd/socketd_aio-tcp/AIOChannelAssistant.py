from socketd.transport.core import Config
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from asyncio import Server
from socketd.transport.core.Codec import  CodecWriter
from socketd.transport.core.Frame import Frame
from socketd.transport.core.codec.Buffer import Buffer
from socketd.transport.core.codec.CodecByteBuffer import ByteBufferCodecWriter, ByteBufferCodecReader


class AIOChannelAssistant(ChannelAssistant):
    def __init__(self, config: Config):
        self.config = config

    async def write(self, source: Server, frame: Frame) -> None:
        writer: CodecWriter = self.config.get_codec().write(frame,
                                                            lambda size: ByteBufferCodecWriter(Buffer(limit=size)))
        # 如果writer不为None，说明写入成功，通过调用source.send()方法将writer.getbuffer()发送给客户端。
        if writer is not None:

            writer.close()

    def is_valid(self, target: Server) -> bool:
        return target.is_serving()

    async def close(self, target: Server) -> None:
        target.close()
        await target.wait_closed()  # 等待消息

    def get_remote_address(self, target: Server) -> str:
        return target.sockets[0].getsockname()

    def get_local_address(self, target: Server) -> str:
        return target.sockets[0].getsockname()[0]

    def read(self, buffer: bytes) -> Frame:
        return self.config.get_codec().read(ByteBufferCodecReader(Buffer(len(buffer), buffer)))
