from typing import Callable, Union, Dict

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Session import Session
from socketd.transport.core.Message import Message


class EventListener(Listener):
    """
    @author bai
    @since 2.0
    """

    def __init__(self):
        self._doOnOpenHandler: Union[Callable[[Session], None], None] = None
        self._doOnMessageHandler: Union[Callable[[Session, Message], None], None] = None
        self._doOnCloseHandler: Union[Callable[[Session], None], None] = None
        self._doOnErrorHandler: Union[Callable[[Session, Exception], None], None] = None
        self._eventRouteSelector: Dict[str, Callable[[Session, Message], None]] = {}

    def do_on_open(self, handler: Callable[[Session], None]) -> 'EventListener':
        self._doOnOpenHandler = handler
        return self

    def do_on_message(self, handler: Callable[[Session, Message], None]) -> 'EventListener':
        self._doOnMessageHandler = handler
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
            self._doOnOpenHandler(session)

    async def on_message(self, session: Session, message: Message):
        if self._doOnMessageHandler:
            self._doOnMessageHandler(session, message)

        if message_handler := self._eventRouteSelector.get(message.event()):
            message_handler(session, message)

    def on_close(self, session: Session):
        if self._doOnCloseHandler:
            self._doOnCloseHandler(session)

    def on_error(self, session: Session, error: Exception):
        if self._doOnErrorHandler:
            self._doOnErrorHandler(session, error)
