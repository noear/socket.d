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
        self._eventRouteSelector: Union[Dict[str, Callable[[Session, Message], None]], None] = None

    def do_on_open(self, _on_open_handler: Callable[[Session, Message], None]) -> 'EventListener':
        self._doOnOpenHandler = _on_open_handler
        return self

    def do_on_message(self, _on_message_handler: Callable[[Session], None]) -> 'EventListener':
        self._doOnMessageHandler = _on_message_handler
        return self

    def do_on_close(self, _on_close_handler: Callable[[Session, Exception], None]) -> 'EventListener':
        self._doOnCloseHandler = _on_close_handler
        return self

    def do_on_error_handler(self, _on_error_handler: Callable[[Session, Exception], None]) -> 'EventListener':
        self._doOnErrorHandler = _on_error_handler
        return self

    def do_on(self, event: str, handler: Callable[[Session, Message], None]):
        self._eventRouteSelector[event] = handler

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
