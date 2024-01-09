import asyncio
from abc import ABC
from loguru import logger

from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.Handshake import Handshake
from socketd.transport.core.Processor import Processor
from socketd.transport.core.Costants import Flag, EntityMetas
from socketd.transport.core.Frame import Frame
from socketd.transport.core.listener.SimpleListener import SimpleListener


class ProcessorDefault(Processor, ABC):

    def __init__(self):
        self.listener = SimpleListener()
        self.log = logger.opt()

    def set_listener(self, listener):
        if listener is not None:
            self.listener = listener

    async def on_receive(self, channel: ChannelInternal, frame):
        if frame.get_flag() == Flag.Connect:
            connectMessage = frame.get_message()
            channel.set_handshake(Handshake(connectMessage))
            await channel.send_connack(connectMessage)
            await self.on_open(channel)
        elif frame.get_flag() == Flag.Connack:
            message = frame.get_message()
            channel.set_handshake(Handshake(message))
            await self.on_open(channel)
        else:
            if channel.get_handshake() is None:
                await channel.close()
                self.log.warning("Channel handshake is None, sessionId={}", channel.get_session().get_session_id())
                return

            channel.set_live_time()

            try:
                if frame.get_flag() == Flag.Ping:
                    await channel.send_pong()
                elif frame.get_flag() == Flag.Pong:
                    pass
                elif frame.get_flag() == Flag.Close:
                    await channel.close()
                    self.on_close(channel)
                elif frame.get_flag() in [Flag.Message, Flag.Request, Flag.Subscribe]:
                    await self.on_receive_do(channel, frame, False)
                elif frame.get_flag() in [Flag.Reply, Flag.ReplyEnd]:
                    await self.on_receive_do(channel, frame, True)
                else:
                    await channel.close()
                    self.on_close(channel)
            except Exception as e:
                logger.error(e)
                self.on_error(channel, e)

    async def on_receive_do(self, channel: ChannelInternal, frame: Frame, isReply):
        fragmentIdxStr = frame.get_message().get_entity().get_meta(EntityMetas.META_DATA_FRAGMENT_IDX)
        if fragmentIdxStr is not None:
            index = int(fragmentIdxStr)
            frameNew: Frame = channel.get_config().get_fragment_handler().aggrFragment(channel, index,
                                                                                       frame.get_message())
            if frameNew is None:
                return
            else:
                frame = frameNew

        if isReply:
            await channel.retrieve(frame, lambda error: self.on_error(channel, error))
        else:
            await self.on_message(channel, frame.get_message())

    async def on_open(self, channel: ChannelInternal):
        await self.listener.on_open(channel.get_session())

    async def on_message(self, channel: ChannelInternal, message):
        await asyncio.get_running_loop().run_in_executor(channel.get_config().get_executor(),
                                                         lambda: asyncio.run(self.listener.on_message(
                                                             channel.get_session(), message)))

    def on_close(self, channel: ChannelInternal):
        self.listener.on_close(channel.get_session())

    def on_error(self, channel: ChannelInternal, error):
        self.listener.on_error(channel.get_session(), error)
