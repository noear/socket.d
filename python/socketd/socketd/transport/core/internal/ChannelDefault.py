import asyncio
from typing import TypeVar, Optional, Callable
from loguru import logger

from websockets import WebSocketCommonProtocol

from socketd.transport.core import Entity
from socketd.transport.core.ChannelInternal import ChannelInternal
from socketd.transport.core.ChannelSupporter import ChannelSupporter
from socketd.transport.core.Message import MessageInternal
from socketd.transport.core.Processor import Processor
from socketd.transport.core.stream.Stream import Stream
from socketd.transport.core.stream.StreamManger import StreamManger, StreamInternal
from socketd.transport.utils.AssertsUtil import AssertsUtil
from socketd.transport.core.internal.ChannelBase import ChannelBase
from socketd.transport.core.Costants import Function, Flag, EntityMetas, Constants
from socketd.transport.core.Session import Session
from socketd.transport.core.internal.SessionDefault import SessionDefault
from socketd.transport.core.Frame import Frame
from socketd.transport.core.entity.MessageDefault import MessageDefault
from socketd.transport.utils.CompletableFuture import CompletableFuture

S = TypeVar("S", bound=WebSocketCommonProtocol)


class ChannelDefault(ChannelBase, ChannelInternal):

    def __init__(self, source: S, supporter: ChannelSupporter[S]):
        ChannelBase.__init__(self, supporter.get_config())
        self.onOpenFuture: Optional[CompletableFuture] = CompletableFuture()
        self._source: WebSocketCommonProtocol = source
        self._assistant = supporter.get_assistant()
        self._processor: Optional[Processor] = supporter.get_processor()
        self._streamManger: StreamManger = supporter.get_config().get_stream_manger()
        self._session: Optional[Session] = None

    def is_valid(self) -> bool:
        return self.is_closed() == 0 and self._assistant.is_valid(self._source)

    def get_remote_address(self) -> str:
        return self._assistant.get_remote_address(self._source)

    def get_local_address(self) -> str:
        return self._assistant.get_local_address(self._source)

    async def send(self, frame: Frame, stream: StreamInternal) -> None:
        AssertsUtil.assert_closed(self)
        if self.get_config().client_mode():
            logger.debug(f"C-SEN:{frame}")
        else:
            logger.debug(f"S-SEN:{frame}")
        with self:
            if frame.get_message() is not None:
                message = frame.get_message()
                # 注册流接收器
                if stream is not None:
                    self._streamManger.add_stream(message.get_sid(), stream)

                # 实体进行分片
                if message.get_entity() is not None:
                    if message.get_entity().get_data_size() > self.get_config().get_fragment_size():
                        message.get_meta_map()[EntityMetas.META_DATA_LENGTH] = str(message.get_data_size())

                    async def __consumer(fragmentEntity: Entity):
                        fragmentFrame: Frame
                        if isinstance(fragmentEntity, MessageInternal):
                            fragmentFrame = Frame(frame.get_flag(), fragmentEntity)
                        else:
                            fragmentFrame = Frame(frame.get_flag(), MessageDefault()
                                                  .set_flag(frame.get_flag())
                                                  .set_sid(message.get_sid())
                                                  .set_entity(fragmentEntity))
                        await self._assistant.write(self._source, fragmentFrame)
                    await self.get_config().get_fragment_handler().split_fragment(self,
                                                                                  stream, message,
                                                                                  __consumer)
                return
            await self._assistant.write(self._source, frame)
            if stream:
                stream.on_progress(True, 1, 1)

    async def retrieve(self, frame: Frame, stream: StreamInternal) -> None:
        """接收（接收答复帧）"""
        if stream:
            if stream.demands() < Constants.DEMANDS_MULTIPLE or frame.get_flag() == Flag.ReplyEnd:
                # 如果是单收或者答复结束，则移除流接收器
                self._streamManger.remove_stream(frame.get_message().get_sid())

            if stream.demands() < Constants.DEMANDS_MULTIPLE:
                await stream.on_reply(frame.get_message())
            else:
                await asyncio.get_running_loop().run_in_executor(self.get_config().get_executor(),
                                                               lambda _m: asyncio.run(stream.on_reply(_m)),
                                                               frame.get_message())
        else:
            logger.debug(
                f"{self.get_config().get_role_name()} stream not found, sid={frame.get_message().get_sid()}, sessionId={self.get_session().get_session_id()}")

    def get_session(self) -> Session:
        if self._session is None:
            self._session = SessionDefault(self)

        return self._session

    async def close(self, code: int = 1000,
                    reason: str = "", ):
        try:
            await super().close()
            await self._assistant.close(self._source)
        except Exception as e:
            logger.warning(f"{self.get_config().get_role_name()} channel close error, "
                           f"sessionId={self.get_session().get_session_id()} : {e}")

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

    def on_error(self, error: Exception):
        self._processor.on_error(self, error)

    def reconnect(self):
        pass
