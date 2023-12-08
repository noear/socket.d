import asyncio
from abc import ABC
from loguru import logger

from .Channel import Channel
from .Handshake import Handshake
from .Processor import Processor
from .Costants import Flag
from .SimpleListener import SimpleListener
from .module.Entity import EntityMetas
from .module.Frame import Frame


class ProcessorDefault(Processor, ABC):

    def __init__(self):
        self.listener = SimpleListener()
        self.log = logger.opt()

    def set_listener(self, listener):
        if listener is not None:
            self.listener = listener

    async def on_receive(self, channel: Channel, frame):
        self.log.debug("on_receive {frame}", frame=frame)
        if frame.get_flag() == Flag.Connect:
            connectMessage = frame.get_message()
            channel.set_handshake(Handshake(connectMessage))
            await channel.send_connack(connectMessage)
            self.on_open(channel.get_session())
        elif frame.get_flag() == Flag.Connack:
            message = frame.get_message()
            channel.set_handshake(Handshake(message))
            self.on_open(channel.get_session())
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
                    self.on_close(channel.get_session())
                elif frame.get_flag() in [Flag.Message, Flag.Request, Flag.Subscribe]:
                    self.on_receive_do(channel, frame, False)
                elif frame.get_flag() in [Flag.Reply, Flag.ReplyEnd]:
                    self.on_receive_do(channel, frame, True)
                else:
                    await channel.close()
                    self.on_close(channel.get_session())
            except Exception as e:
                logger.warning(e)
                self.on_error(channel.get_session(), e)

    def on_receive_do(self, channel: Channel, frame: Frame, isReply):
        fragmentIdxStr = frame.get_message().get_entity().get_meta(EntityMetas.META_DATA_FRAGMENT_IDX)
        if fragmentIdxStr is not None:
            index = int(fragmentIdxStr)
            frameNew = channel.get_config().get_fragment_handler().aggrFragment(channel, index, frame)
            if frameNew is None:
                return
            else:
                frame = frameNew

        if isReply:
            channel.retrieve(frame, lambda error:self.on_error(channel, error))
        else:
            self.on_message(channel, frame.get_message())

    def on_open(self, session):
        self.listener.on_open(session)

    def on_message(self, channel: Channel, message):
        # self.listener.on_message(channel.get_session(), message)
        channel.get_config().get_executor().submit(lambda _message: asyncio.run(self.listener.on_message(channel.get_session(), _message)), message)

    def on_close(self, session):
        self.listener.on_close(session)

    def on_error(self, session, error):
        self.listener.on_error(session, error)
