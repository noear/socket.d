from abc import ABC
from typing import Optional, Union

from socketd.exception.SocketDExecption import SocketDAlarmException
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.HandshakeDefault import HandshakeDefault
from socketd.transport.core.Message import Message
from socketd.transport.core.Processor import Processor
from socketd.transport.core.Costants import Flag, EntityMetas, Constants
from socketd.transport.core.Frame import Frame
from socketd.transport.core.listener.SimpleListener import SimpleListener
from socketd.transport.stream.Stream import Stream
from socketd.transport.stream.StreamManger import StreamInternal
from socketd.transport.utils.AsyncUtil import AsyncUtil

from socketd.transport.core.config.logConfig import log


class ProcessorDefault(Processor, ABC):

    def __init__(self):
        self.listener = SimpleListener()

    def set_listener(self, listener):
        if listener is not None:
            self.listener = listener

    async def on_receive(self, channel: ChannelInternal, frame):
        if channel.get_config().client_mode():
            log.debug(f"C-REV:{frame}")
        else:
            log.debug(f"S-REV:{frame}")

        if frame.get_flag() == Flag.Connect:
            connectMessage = frame.get_message()
            channel.set_handshake(HandshakeDefault(connectMessage))

            async def _future(b: bool, e: Exception):
                if channel.is_valid():
                    if e is None:
                        try:
                            await channel.send_connack(connectMessage)
                        except Exception as _e:
                            self.on_error(channel, _e)
                    else:
                        # 如果还有效，则关闭通道
                        await channel.close(Constants.CLOSE3_ERROR)
                        self.on_close_internal(channel)
            await channel.on_open_future(_future)
            await self.on_open(channel)
        elif frame.get_flag() == Flag.Connack:
            message = frame.get_message()
            channel.set_handshake(HandshakeDefault(message))
            await self.on_open(channel)
        else:
            if channel.get_handshake() is None:
                await channel.close(Constants.CLOSE11_PROTOCOL)
                self.log.warning("Channel handshake is None, sessionId={}", channel.get_session().get_session_id())
                return

            channel.set_live_time()

            try:
                if frame.get_flag() == Flag.Ping:
                    await channel.send_pong()
                elif frame.get_flag() == Flag.Pong:
                    pass
                elif frame.get_flag() == Flag.Close:
                    await channel.close(Constants.CLOSE11_PROTOCOL)
                    self.on_close(channel)
                elif frame.get_flag() == Flag.Alarm:
                    e = SocketDAlarmException(frame.get_message())
                    stream: Union[StreamInternal, Stream] = channel.get_config().get_stream_manger() \
                        .get_stream(frame.get_message().get_sid())
                    if stream:
                        self.on_error(channel, e)
                    else:
                        channel.get_config().get_stream_manger().remove_stream(frame.message().sid())
                        stream.on_error(e)
                elif frame.get_flag() in [Flag.Message, Flag.Request, Flag.Subscribe]:
                    await self.on_receive_do(channel, frame, False)
                elif frame.get_flag() in [Flag.Reply, Flag.ReplyEnd]:
                    await self.on_receive_do(channel, frame, True)
                else:
                    await channel.close(Constants.CLOSE12_PROTOCOL_ILLEGAL)
                    self.on_close(channel)
            except Exception as e:
                log.error(e)
                self.on_error(channel, e)
                raise e

    async def on_receive_do(self, channel: ChannelInternal, frame: Frame, isReply):
        stream: Optional[StreamInternal] = None
        streamTotal: int = 1
        streamIndex: int = 0
        if isReply:
            stream = channel.get_stream(frame.get_message().get_sid())

        if channel.get_config().get_fragment_handler().aggrEnable():
            fragmentIdxStr = frame.get_message().get_entity().get_meta(EntityMetas.META_DATA_FRAGMENT_IDX)
            if fragmentIdxStr is not None:
                del streamIndex
                streamIndex = int(fragmentIdxStr)
                frameNew: Frame = channel.get_config().get_fragment_handler().aggrFragment(channel,
                                                                                           streamIndex,
                                                                                           frame.get_message())
                if stream:
                    del streamTotal
                    streamTotal = int(frame.get_message().get_meta_or_default(EntityMetas.META_DATA_FRAGMENT_TOTAL, 0))
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
            await channel.retrieve(frame, stream)
        else:
            await self.on_message(channel, frame.get_message())

    async def on_open(self, channel: ChannelInternal):
        try:
            channel.do_open_future(True, None)
            await self.listener.on_open(channel.get_session())
        except Exception as e:
            channel.do_open_future(True, e)
            log.warning(e)

    async def on_message(self, channel: ChannelInternal, message: Message):
        AsyncUtil.thread_loop(self.listener.on_message(
            channel.get_session(), message),
            pool=channel.get_config().get_executor())

    def on_close(self, channel: ChannelInternal):
        self.listener.on_close(channel.get_session())

    def on_error(self, channel: ChannelInternal, error):
        self.listener.on_error(channel.get_session(), error)

    def on_close_internal(self, channel: ChannelInternal):
        self.listener.on_close(channel.get_session())
