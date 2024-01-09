from abc import ABC, abstractmethod
from typing import Callable

from socketd.transport.core.Channel import Channel
from socketd.transport.core.Session import Session


class ChannelInternal(Channel, ABC):

    @abstractmethod
    def set_session(self, session: Session): ...

    @abstractmethod
    def get_stream(self, sid: str): ...

    @abstractmethod
    def on_open_future(self, future: Callable[[bool, Exception], None]): ...

    @abstractmethod
    def do_open_future(self, is_ok: bool, e: Exception): ...
