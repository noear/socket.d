from typing import Callable, Union, Dict, Coroutine

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Session import Session
from socketd.transport.core.Message import Message
from socketd.utils.RunUtils import RunUtils


class EventListener(Listener):
    """
    @author bai
    @since 2.0
    """

    def __init__(self):
        self._doOnOpenHandler: Union[Callable[[Session], Coroutine], None] = None
        self._doOnMessageHandler: Union[Callable[[Session, Message], Coroutine], None] = None
        self._doOnReplyHandler: Union[Callable[[Session, Message], Coroutine], None] = None
        self._doOnSendHandler: Union[Callable[[Session, Message], Coroutine], None] = None

        self._doOnCloseHandler: Union[Callable[[Session], Coroutine], None] = None
        self._doOnErrorHandler: Union[Callable[[Session, Exception], Coroutine], None] = None
        self._eventRouteSelector: Dict[str, Callable[[Session, Message], Coroutine]] = {}

    def do_on_open(self, handler: Callable[[Session], None]) -> 'EventListener':
        self._doOnOpenHandler = handler
        return self

    def do_on_message(self, handler: Callable[[Session, Message], None]) -> 'EventListener':
        self._doOnMessageHandler = handler
        return self

    def do_on_reply(self, handler: Callable[[Session, Message], None]) -> 'EventListener':
        self._doOnReplyHandler = handler
        return self

    def do_on_send(self, handler: Callable[[Session, Message], None]) -> 'EventListener':
        self._doOnSendHandler = handler
        return self

    def do_on_close(self, handler: Callable[[Session], None]) -> 'EventListener':
        self._doOnCloseHandler = handler
        return self

    def do_on_error(self, handler: Callable[[Session, Exception], None]) -> 'EventListener':
        self._doOnErrorHandler = handler
        return self

    def do_on(self, event: str, handler: Callable[[Session, Message], None]) -> 'EventListener':
        self._eventRouteSelector[event] = handler
        return self

    async def on_open(self, session: Session):
        if self._doOnOpenHandler:
            await RunUtils.waitTry(self._doOnOpenHandler(session))

    async def on_message(self, session: Session, message: Message):
        if self._doOnMessageHandler:
            await RunUtils.waitTry(self._doOnMessageHandler(session, message))

        if message_handler := self._eventRouteSelector.get(message.event()):
            await RunUtils.waitTry(message_handler(session, message))

    async def on_reply(self, session: Session, message: Message):
        if self._doOnReplyHandler:
            await RunUtils.waitTry(self._doOnReplyHandler(session, message))

    async def on_send(self, session: Session, message: Message):
        if self._doOnSendHandler:
            await RunUtils.waitTry(self._doOnSendHandler(session, message))

    async def on_close(self, session: Session):
        if self._doOnCloseHandler:
            await RunUtils.waitTry(self._doOnCloseHandler(session))

    async def on_error(self, session: Session, error: Exception):
        if self._doOnErrorHandler:
             await RunUtils.waitTry(self._doOnErrorHandler(session, error))
