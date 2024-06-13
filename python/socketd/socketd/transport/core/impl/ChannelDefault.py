import asyncio
import time
import traceback
from typing import TypeVar, Optional

from socketd.transport.core import Entity
from socketd.transport.core.Asserts import Asserts
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.ChannelSupporter import ChannelSupporter
from socketd.transport.core.Message import MessageInternal, Message
from socketd.transport.core.entity.MessageBuilder import MessageBuilder
from socketd.utils.LogConfig import log
from socketd.transport.stream.Stream import Stream, StreamInternal
from socketd.transport.stream.StreamManger import StreamManger
from socketd.transport.core.impl.ChannelBase import ChannelBase
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Flags import Flags
from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Session import Session
from socketd.transport.core.impl.SessionDefault import SessionDefault
from socketd.transport.core.Frame import Frame
from socketd.utils.CompletableFuture import CompletableFuture
from socketd.utils.RunUtils import RunUtils

S = TypeVar("S")


class ChannelDefault(ChannelBase, ChannelInternal):

    def __init__(self, source: S, supporter: ChannelSupporter[S]):
        ChannelBase.__init__(self, supporter.get_config())

        self.onOpenFuture: Optional[CompletableFuture] = CompletableFuture()

        self._source = source
        self._processor = supporter.get_processor()
        self._assistant = supporter.get_assistant()
        self._streamManger: StreamManger = supporter.get_config().get_stream_manger()

        self._session: Optional[Session] = None
        self._liveTime: Optional[float] = None
        self._closeCode: int = 0
        self._isCloseNotified = False
        self._alarmCode: int = 0

    def is_valid(self) -> bool:
        return self.close_code() == 0 and self._assistant.is_valid(self._source)

    def is_closing(self) -> bool:
        return self._closeCode == Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING

    def close_code(self):
        if self._closeCode > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING:
            return self._closeCode
        else:
            return 0

    def get_live_time(self):
        return self._liveTime

    def set_live_time_as_now(self):
        self._liveTime = time.time()

    def set_alarm_code(self, alarm_code:int):
        self._alarmCode = alarm_code

    def get_remote_address(self) -> str:
        return self._assistant.get_remote_address(self._source)

    def get_local_address(self) -> str:
        return self._assistant.get_local_address(self._source)

    async def send(self, frame: Frame, stream: StreamInternal) -> None:
        Asserts.assert_closed(self)

        if self.get_config().client_mode():
            log.debug(f"C-SEN:{frame}")
        else:
            log.debug(f"S-SEN:{frame}")

        with self:
            await self.send_do(frame, stream)

    async def send_do(self, frame: Frame, stream: StreamInternal):
        if self._alarmCode == Constants.ALARM3001_PRESSURE:
            if frame.flag() >= Flags.Message and frame.flag() <= Flags.Subscribe:
                if frame.message().meta(EntityMetas.META_X_UNLIMITED) is None:
                    try:
                        log.debug("Too much pressure, sleep=100ms")
                        self.set_alarm_code(0)
                        time.sleep(0.1)
                    except Exception as e:
                        ... #略过

        if frame.message() is not None:
            message: Message = frame.message()
            # 注册流接收器
            if stream is not None:
                self._streamManger.add_stream(message.sid(), stream)

            # 实体进行分片
            if message.entity() is not None:
                if message.entity().data_size() > self.get_config().get_fragment_size():
                    message.put_meta(EntityMetas.META_DATA_LENGTH, str(message.data_size()))

                async def __consumer(fragmentEntity: Entity):
                    fragmentFrame: Frame
                    if isinstance(fragmentEntity, MessageInternal):
                        fragmentFrame = Frame(frame.flag(), fragmentEntity)
                    else:
                        messageNew = MessageBuilder().flag(frame.flag()).sid(message.sid()).event(
                            message.event()).entity(fragmentEntity).build()
                        fragmentFrame = Frame(frame.flag(), messageNew)
                    await self._processor.send_frame(self, fragmentFrame, self._assistant, self._source)

                await self.get_config().get_fragment_handler().split_fragment(self, stream, message, __consumer)
            return

        await self._processor.send_frame(self, frame, self._assistant, self._source)
        if stream is not None:
            stream.on_progress(True, 1, 1)

    def reconnect(self):
        ...

    def on_error(self, error):
        self._processor.on_error(self, error)

    def get_session(self) -> Session:
        if self._session is None:
            self._session = SessionDefault(self)

        return self._session

    def set_session(self, __session: Session):
        self._session = __session

    def get_stream(self, sid: str) -> Stream:
        return self._streamManger.get_stream(sid)

    async def on_open_future(self, future):
        try:
            self.onOpenFuture.then_async_callback(future)
        except Exception as e:
            await future(None, e)
        return self.onOpenFuture

    def do_open_future(self, is_ok: bool, e: Exception):
        if is_ok:
            self.onOpenFuture.accept(is_ok)
        else:
            self.onOpenFuture.cancel()
            self.onOpenFuture.set_e(e)

    async def close(self, code):
        try:
            self._closeCode = code

            await RunUtils.waitTry(super().close(code))

            if code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING:
                if self._assistant.is_valid(self._source):
                    # 如果有效且非预关闭，则尝试关闭源
                    await RunUtils.waitTry(self._assistant.close(self._source))
                    log.debug(f"{self.get_config().get_role_name()} channel closed, sessionId={self.get_session().session_id()}")
        except Exception as e:
            e_msg = traceback.format_exc()
            log.warning(f"{self.get_config().get_role_name()} channel close error, sessionId={self.get_session().session_id()} \n{e_msg}")

        if code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING:
            self.on_close_do()


    def on_close_do(self):
        if self._isCloseNotified == False:
            self._isCloseNotified = True
            self._processor.do_close_notice(self)

