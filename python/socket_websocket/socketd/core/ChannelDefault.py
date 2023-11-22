from abc import ABC
from typing import Dict, Union, Generator, TypeVar

from websockets import WebSocketCommonProtocol

from socketd.core.AssertsUtil import AssertsUtil
from socketd.core.ChannelBase import ChannelBase
from socketd.core.Costants import Function, Flag
from socketd.core.Session import Session
from socketd.core.SessionDefault import SessionDefault
from socketd.core.ThreadSafeDict import ThreadSafeDict
from socketd.core.config.Config import Config
from socketd.core.module.Frame import Frame
from socketd.core.module.MessageDefault import MessageDefault
from socketd.transport.ChannelAssistant import ChannelAssistant

S = TypeVar("S", bound=WebSocketCommonProtocol)

thread_safe_dict = ThreadSafeDict()


class ChannelDefault(ChannelBase):

    def __init__(self, source: S, config: Config, assistant: ChannelAssistant):
        ChannelBase.__init__(self, config)
        self.source: WebSocketCommonProtocol = source
        self.assistant = assistant
        self.acceptorMap = {}
        self.session: Session = None

    def remove_acceptor(self, sid: str):
        self.acceptorMap.pop(sid)

    def is_valid(self) -> bool:
        return self.assistant.is_valid(self.source)

    def get_remote_address(self) -> str:
        return self.assistant.get_remote_address(self.source)

    def get_local_address(self) -> str:
        return self.assistant.get_local_address(self.source)

    async def send(self, frame: Frame, acceptor: Function) -> None:
        AssertsUtil.assert_closed(self)
        if frame.get_message() is not None:
            message = frame.get_message()

            if acceptor is not None:
                self.acceptorMap[message.get_sid()] = acceptor

            if message.get_entity() is not None:
                if message.get_entity().get_data_size() > Config.MAX_SIZE_FRAGMENT:
                    fragmentIndex = thread_safe_dict.set("AtomicReference", 0)
                    while True:
                        fragmentEntity = self.get_config().get_fragment_handler().nextFragment(self.get_config(),
                                                                                               fragmentIndex,
                                                                                               message.get_entity())
                        if fragmentEntity is not None:
                            fragmentFrame = Frame(frame.get_flag(), MessageDefault()
                                                  .flag(frame.get_flag())
                                                  .sid(message.get_sid())
                                                  .entity(fragmentEntity))
                            await self.assistant.write(self.source, fragmentFrame)
                        else:
                            return
                else:
                    await self.assistant.write(self.source, frame)
                    return

        await self.assistant.write(self.source, frame)

    def retrieve(self, frame: Frame) -> None:
        acceptor = self.acceptorMap.get(frame.get_message().get_sid())

        if acceptor is not None:
            if acceptor.isSingle() or frame.get_flag() == Flag.ReplyEnd:
                self.acceptorMap.pop(frame.get_message().get_sid())

            acceptor.accept(frame.get_message())

    def get_session(self) -> Session:
        if self.session is None:
            self.session = SessionDefault(self)

        return self.session

    async def close(self, code: int = 1000,
                    reason: str = "", ):
        await super().close()
        self.acceptorMap.clear()
        await self.assistant.close(self.source)
