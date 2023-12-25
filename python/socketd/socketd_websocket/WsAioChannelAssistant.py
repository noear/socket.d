import asyncio

from socketd.core.Buffer import Buffer
from socketd.core.config.Config import Config
from socketd.core.module.Frame import Frame
from websockets.server import WebSocketServerProtocol
from websockets.protocol import State
from socketd.transport.ChannelAssistant import ChannelAssistant


class WsAioChannelAssistant(ChannelAssistant):
    def __init__(self, config: Config):
        self.config = config
        self.__loop = asyncio.get_event_loop()

    async def write(self, source: WebSocketServerProtocol, frame: Frame) -> None:
        # writer: Buffer = await self.__loop.run_in_executor(self.config.get_executor(), lambda _frame:
        # self.config.get_codec().write(frame, lambda size: Buffer(limit=size)), frame)
        # 这里使用了异步方式调用self.__loop.run_in_executor()来执行一个匿名函数，
        # 该匿名函数的参数是一个帧（frame），然后调用self.config.get_codec().write()方法来将帧（frame）写入缓冲区，
        # 其中lambda size: Buffer(limit=size)是一个匿名函数，用于创建一个容量为size的缓冲区。将得到的缓冲区对象赋值给writer变量。
        writer: Buffer = self.config.get_codec().write(frame, lambda size: Buffer(limit=size))
        # 如果writer不为None，说明写入成功，通过调用source.send()方法将writer.getbuffer()发送给客户端。
        if writer is not None:
            await source.send(writer.getbuffer())

    def is_valid(self, target: WebSocketServerProtocol) -> bool:
        return target.state == State.OPEN

    async def close(self, target: WebSocketServerProtocol) -> None:
        # await target.wait_closed()  # 等待消息
        await target.close()

    def get_remote_address(self, target: WebSocketServerProtocol) -> str:
        return target.remote_address

    def get_local_address(self, target: WebSocketServerProtocol) -> str:
        return target.local_address

    def read(self, buffer: bytearray) -> Frame:
        return self.config.get_codec().read(Buffer(buffer))
