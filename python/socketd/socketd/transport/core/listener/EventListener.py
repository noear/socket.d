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
        self._do_on_open_handler: Union[Callable[[Session], None], None] = None
        self._do_on_message_handler: Union[Callable[[Session, Message], None], None] = None
        self._do_on_close_handler: Union[Callable[[Session], None], None] = None
        self._do_on_error_handler: Union[Callable[[Session, Exception], None], None] = None
        self._do_on_message_routing: Union[Dict[str, Callable[[Session, Message], None]], None] = None

    def do_on_open_handler(self, _on_open_handler: Callable[[Session, Message], None]) -> 'EventListener':
        self._do_on_open_handler = _on_open_handler
        return self

    def do_on_message_handler(self, _on_message_handler: Callable[[Session], None]) -> 'EventListener':
        self._do_on_message_handler = _on_message_handler
        return self

    def do_on_close_handler(self, _on_close_handler: Callable[[Session, Exception], None]) -> 'EventListener':
        self._do_on_close_handler = _on_close_handler
        return self

    def do_on_error_handler(self, _on_error_handler: Callable[[Session, Exception], None]) -> 'EventListener':
        self._do_on_error_handler = _on_error_handler
        return self

    def do_on(self, event: str, handler: Callable[[Session, Message], None]):
        self._do_on_message_routing[event] = handler

    async def on_open(self, session: Session):
        if self._do_on_open_handler:
            self._do_on_open_handler(session)

    async def on_message(self, session: Session, message: Message):
        if self._do_on_message_handler:
            self._do_on_message_handler(session, message)

        if message_handler := self._do_on_message_routing.get(message.event()):
            message_handler(session, message)

    def on_close(self, session: Session):
        if self._do_on_close_handler:
            self._do_on_close_handler(session)

    def on_error(self, session: Session, error: Exception):
        if self._do_on_error_handler:
            self._do_on_error_handler(session, error)
