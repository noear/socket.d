from abc import ABC, abstractmethod
from typing import TypeVar, Callable

from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Frame import Frame

S = TypeVar("S")

# 帧输入输出处理器 # 为 TrafficLimiter 提供支持
class FrameIoHandler(ABC):
    # 发送帧处理
    @abstractmethod
    async def send_frame_handle(self, channel: ChannelInternal, frame: Frame, channelAssistant: ChannelAssistant[S],
                          target: S, completionHandler:Callable[[bool, Exception], None]):
        ...

    # 接收帧处理
    @abstractmethod
    async def reve_frame_handle(self, channel: ChannelInternal, frame: Frame):
        ...