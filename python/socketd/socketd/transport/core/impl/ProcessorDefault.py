import asyncio
from abc import ABC
from typing import Optional, Union

from loguru import logger

from socketd.exception.SocketDExecption import SocketDAlarmException, SocketDConnectionException
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.HandshakeDefault import HandshakeDefault
from socketd.transport.core.Message import Message
from socketd.transport.core.Processor import Processor
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Flags import Flags
from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Frame import Frame
from socketd.transport.core.listener.SimpleListener import SimpleListener
from socketd.transport.stream.Stream import Stream
from socketd.transport.stream.StreamManger import StreamInternal
from socketd.transport.utils.AsyncUtil import AsyncUtil


class ProcessorDefault(Processor, ABC):

    def __init__(self):
        self.listener = SimpleListener()
        self.log = logger.opt()

    def set_listener(self, listener):
        if listener is not None:
            self.listener = listener

    async def on_receive(self, channel: ChannelInternal, frame):
        if channel.get_config().client_mode():
            logger.debug(f"C-REV:{frame}")
        else:
            logger.debug(f"S-REV:{frame}")

        if frame.flag() == Flags.Connect:
            # if server
            connectMessage = frame.message()
            channel.set_handshake(HandshakeDefault(connectMessage))

            async def _future(r: bool, e: Exception):
                if r:
                    #如果无异常
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

                self.log.warning("{} channel handshake is None, sessionId={}", channel.get_config().get_role_name(), channel.get_session().session_id())
                return

            # 更新最后活动时间
            channel.set_live_time_as_now()

            try:
                if frame.flag() == Flags.Ping:
                    await channel.send_pong()
                elif frame.flag() == Flags.Pong:
                    pass
                elif frame.flag() == Flags.Close:
                    code:int = 0

                    if frame.message()  is not None:
                        code = frame.message().meta_as_int("code")

                    if code == 0:
                        code = Constants.CLOSE1001_PROTOCOL_CLOSE

                    await self.on_close_internal(channel, code)
                elif frame.flag() == Flags.Alarm:
                    e = SocketDAlarmException(frame.message())
                    stream: StreamInternal = channel.get_config().get_stream_manger().get_stream(frame.message().sid())

                    if stream is None:
                        self.on_error(channel, e)
                    else:
                        channel.get_config().get_stream_manger().remove_stream(frame.message().sid())
                        stream.on_error(e)
                elif frame.flag() == Flags.Pressure:
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
            channel.retrieve(frame, stream)
        else:
            self.on_message(channel, frame.message())

    def on_open(self, channel: ChannelInternal):
        asyncio.create_task(self.on_open_do(channel))

    async def on_open_do(self, channel: ChannelInternal):
        try:
            await self.listener.on_open(channel.get_session())
            channel.do_open_future(True, None)
        except Exception as e:
            logger.warning("{} channel listener onOpen error", channel.get_config().get_role_name(), e)
            channel.do_open_future(False, e)

    def on_message(self, channel: ChannelInternal, message: Message):
        asyncio.create_task(self.on_message_do(channel, message))

    async def on_message_do(self, channel: ChannelInternal, message: Message):
        try:
            await self.listener.on_message(channel.get_session(), message)
        except Exception as e:
            logger.warning("{} channel listener onMessage error", channel.get_config().get_role_name(), e)
            self.on_error(channel, e)

    def on_close(self, channel: ChannelInternal):
        if channel.is_closed() <= Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING:
            self.on_close_internal(channel, Constants.CLOSE2003_DISCONNECTION)

    async def on_close_internal(self, channel: ChannelInternal, code: int):
        await channel.close(code)

        if code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING:
            await self.listener.on_close(channel.get_session())

    def on_error(self, channel: ChannelInternal, error):
        self.listener.on_error(channel.get_session(), error)

