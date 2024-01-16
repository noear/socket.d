import asyncio
import math

from websockets import WebSocketClientProtocol, frames

from socketd.transport.core.Buffer import Buffer
from socketd.transport.core.Config import Config
from socketd.transport.core.Frame import Frame
from websockets.server import WebSocketServerProtocol
from websockets.protocol import State
from socketd.transport.core.ChannelAssistant import ChannelAssistant


class WsAioChannelAssistant(ChannelAssistant):
    def __init__(self, config: Config):
        self.config = config

    async def write(self, source: WebSocketClientProtocol, frame: Frame) -> None:
        writer: Buffer = self.config.get_codec().write(frame, lambda size: Buffer(limit=size))
        # 如果writer不为None，说明写入成功，通过调用source.send()方法将writer.getbuffer()发送给客户端。
        if writer is not None:
            _data = writer.getvalue()
            _len = len(writer.getbuffer())
            if _len > source.max_size:
                count = _len / source.max_size if (_len % source.max_size) > 0 else _len / source.max_size + 1
                _start = 0
                _steam = []
                for i in range(1, math.ceil(count)):
                    _end = source.max_size * i
                    await source.write_frame(False, frames.OP_BINARY, _data[_start:_end])
                    _start += source.max_size
                await source.write_frame(True, frames.OP_CONT, b"")
            else:
                await source.send(_data)
            writer.close()

    def is_valid(self, target: WebSocketServerProtocol) -> bool:
        return target.state == State.OPEN

    async def close(self, target: WebSocketServerProtocol) -> None:
        await target.close()
        await target.wait_closed()  # 等待消息

    def get_remote_address(self, target: WebSocketServerProtocol) -> str:
        return target.remote_address

    def get_local_address(self, target: WebSocketServerProtocol) -> str:
        return target.local_address

    def read(self, buffer: bytearray) -> Frame:
        return self.config.get_codec().read(Buffer(len(buffer), buffer))
