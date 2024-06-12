import asyncio
import traceback
from abc import ABC
from typing import Optional, Callable, TypeVar

from socketd.exception.SocketDExecption import SocketDAlarmException, SocketDConnectionException
from socketd.transport.core.ChannelAssistant import ChannelAssistant
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.FrameIoHandler import FrameIoHandler
from socketd.transport.core.HandshakeDefault import HandshakeDefault
from socketd.transport.core.Message import Message
from socketd.transport.core.Processor import Processor
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Flags import Flags
from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Frame import Frame
from socketd.utils.LogConfig import log
from socketd.transport.core.listener.SimpleListener import SimpleListener
from socketd.transport.stream.Stream import StreamInternal
from socketd.utils.RunUtils import RunUtils

S = TypeVar("S")

# 协议处理器默认实现（原则上，写不要在读的线程上执行）
class ProcessorDefault(Processor, FrameIoHandler, ABC):

    def __init__(self):
        self.listener = SimpleListener()

    def set_listener(self, listener):
        if listener is not None:
            self.listener = listener

    async def send_frame(self, channel: ChannelInternal, frame: Frame, channelAssistant: ChannelAssistant[S], target: S):
        def completionHandler(result:bool, throwable:Exception):
            ...

        await self.send_frame_handle(channel, frame, channelAssistant, target, completionHandler)

    async def send_frame_handle(self, channel: ChannelInternal, frame: Frame, channelAssistant: ChannelAssistant[S],
                          target: S, completionHandler:Callable[[bool, Exception], None]):
        await channelAssistant.write(target, frame)

        if frame.flag() >= Flags.Message:
            await RunUtils.waitTry(self.listener.on_send(channel.get_session(), frame.message()))

    async def reve_frame(self, channel: ChannelInternal, frame):
        await self.reve_frame_handle(channel, frame)

    async def reve_frame_handle(self, channel: ChannelInternal, frame: Frame):
        if channel.get_config().client_mode():
            log.debug(f"C-REV:{frame}")
        else:
            log.debug(f"S-REV:{frame}")

        if frame.flag() == Flags.Connect:
            # if server
            connectMessage = frame.message()
            channel.set_handshake(HandshakeDefault(connectMessage))

            async def _future(r: bool, e: Exception):
                if r:
                    # 如果无异常
                    if channel.is_valid():
                        try:
                            await channel.send_connack(connectMessage)
                        except Exception as _e:
                            self.on_error(channel, _e)
                else:
                    # 如果有异常
                    if channel.is_valid():
                        # 如果还有效，则关闭通道
                        await self.on_close_internal(channel, Constants.CLOSE2001_ERROR)

            await channel.on_open_future(_future)
            self.on_open(channel)
        elif frame.flag() == Flags.Connack:
            # if client
            channel.set_handshake(HandshakeDefault(frame.message()))
            self.on_open(channel)
        else:
            if channel.get_handshake() is None:
                await channel.close(Constants.CLOSE1002_PROTOCOL_ILLEGAL)

                if frame.flag() == Flags.Close:
                    raise SocketDConnectionException("Connection request was rejected")

                log.warning(f"{channel.get_config().get_role_name()} channel handshake is None, sessionId={channel.get_session().session_id()}")
                return

            # 更新最后活动时间
            channel.set_live_time_as_now()

            try:
                if frame.flag() == Flags.Ping:
                    RunUtils.taskTry(channel.send_pong())
                elif frame.flag() == Flags.Pong:
                    pass
                elif frame.flag() == Flags.Close:
                    code: int = 0

                    if frame.message() is not None:
                        code = frame.message().meta_as_int("code")

                    if code == 0:
                        code = Constants.CLOSE1001_PROTOCOL_CLOSE

                    await self.on_close_internal(channel, code)
                elif frame.flag() == Flags.Alarm:
                    e = SocketDAlarmException(frame.message())
                    channel.set_alarm_code(e.get_alarm_code())

                    stream: StreamInternal = channel.get_config().get_stream_manger().get_stream(frame.message().sid())

                    if stream is None:
                        self.on_error(channel, e)
                    else:
                        channel.get_config().get_stream_manger().remove_stream(frame.message().sid())
                        RunUtils.taskTry(stream.on_error(e))
                elif frame.flag() == Flags.Pressure:
                    code = frame.message().meta_as_int("code")
                    channel.set_alarm_code(code)
                    pass
                elif frame.flag() in [Flags.Message, Flags.Request, Flags.Subscribe]:
                    await self.on_receive_do(channel, frame, False)
                elif frame.flag() in [Flags.Reply, Flags.ReplyEnd]:
                    await self.on_receive_do(channel, frame, True)
                else:
                    await self.on_close_internal(channel, Constants.CLOSE1002_PROTOCOL_ILLEGAL)
            except Exception as e:
                self.on_error(channel, e)

    async def on_receive_do(self, channel: ChannelInternal, frame: Frame, isReply):
        stream: Optional[StreamInternal] = None
        streamTotal: int = 1
        streamIndex: int = 0
        if isReply:
            stream = channel.get_stream(frame.message().sid())

        # 如果启用了聚合!
        if channel.get_config().get_fragment_handler().aggr_enable():
            # 尝试聚合分片处理
            fragmentIdxStr = frame.message().entity().meta(EntityMetas.META_DATA_FRAGMENT_IDX)
            if fragmentIdxStr is not None:
                # 解析分片索引
                del streamIndex
                streamIndex = int(fragmentIdxStr)
                frameNew: Frame = channel.get_config().get_fragment_handler().aggr_fragment(channel,
                                                                                            streamIndex,
                                                                                            frame.message())
                if stream:
                    # 解析分片总数
                    del streamTotal
                    streamTotal = int(frame.message().meta_or_default(EntityMetas.META_DATA_FRAGMENT_TOTAL, 0))
                if frameNew is None:
                    if stream:
                        stream.on_progress(False, streamIndex, streamTotal)
                    return
                else:
                    del frame
                    frame = frameNew

        if isReply:
            if stream:
                stream.on_progress(False, streamIndex, streamTotal)
            await self.on_reply(channel, frame, stream)
        else:
            self.on_message(channel, frame)

    def on_open(self, channel: ChannelInternal):
        RunUtils.taskTry(self.on_open_do(channel))

    async def on_open_do(self, channel: ChannelInternal):
        try:
            await RunUtils.waitTry(self.listener.on_open(channel.get_session()))
            channel.do_open_future(True, None)
        except Exception as e:
            e_msg = traceback.format_exc()
            log.warning(f"{channel.get_config().get_role_name()} channel listener onOpen error \n{e_msg}")
            channel.do_open_future(False, e)

    def on_message(self, channel: ChannelInternal, frame: Frame):
        RunUtils.taskTry(self.on_message_do(channel, frame.message()))

    async def on_message_do(self, channel: ChannelInternal, message: Message):
        try:
            await RunUtils.waitTry(self.listener.on_message(channel.get_session(), message))
        except Exception as e:
            e_msg = traceback.format_exc()
            log.warning(f"{channel.get_config().get_role_name()} channel listener onMessage error \n{e_msg}")
            self.on_error(channel, e)

    async def on_reply(self, channel: ChannelInternal, frame: Frame, stream: StreamInternal) -> None:
        """接收（接收答复帧）"""
        if stream is not None:
            if stream.demands() < Constants.DEMANDS_MULTIPLE or frame.flag() == Flags.ReplyEnd:
                # 如果是单收或者答复结束，则移除流接收器
                channel.get_config().get_stream_manger().remove_stream(frame.message().sid())

            if stream.demands() < Constants.DEMANDS_MULTIPLE:
                # 单收时，内部已经是异步机制
                await RunUtils.waitTry(stream.on_reply(frame.message()))
                await RunUtils.waitTry(self.listener.on_reply(channel.get_session(), frame.message()))
            else:
                # 改为异步处理，避免卡死Io线程
                asyncio.get_running_loop().run_in_executor(self.get_config().get_exchange_executor(),
                                                           lambda _m: asyncio.run(stream.on_reply(_m)), frame.message())
        else:
            await RunUtils.waitTry(self.listener.on_reply(channel.get_session(), frame.message()))
            log.debug(
                f"{channel.get_config().get_role_name()} stream not found, sid={frame.message().sid()}, sessionId={channel.get_session().session_id()}")

    def on_close(self, channel: ChannelInternal):
        if channel.close_code() <= Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING:
            RunUtils.taskTry(self.on_close_internal(channel, Constants.CLOSE2003_DISCONNECTION))

    async def on_close_internal(self, channel: ChannelInternal, code: int):
        await channel.close(code)

    def on_error(self, channel: ChannelInternal, error):
        RunUtils.taskTry(self.on_error_internal(channel, error))

    async def on_error_internal(self, channel: ChannelInternal, error):
        try:
            await RunUtils.waitTry(self.listener.on_error(channel.get_session(), error))
        except Exception as e:
            e_msg = traceback.format_exc()
            log.warning(f"{channel.get_config().get_role_name()} channel listener onError error \n{e_msg}")

    def do_close_notice(self, channel: ChannelInternal):
        RunUtils.taskTry(self.do_close_notice_internal(channel))

    async def do_close_notice_internal(self, channel: ChannelInternal):
        try:
            await RunUtils.waitTry(self.listener.on_close(channel.get_session()))
        except Exception as e:
            self.on_error(channel, e)
