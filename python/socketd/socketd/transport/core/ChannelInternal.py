from abc import ABC, abstractmethod
from typing import Callable, Optional

from socketd.transport.core.Channel import Channel
from socketd.transport.core.Costants import Function
from socketd.transport.core.Session import Session


class ChannelInternal(Channel, ABC):
    @abstractmethod
    def set_session(self, session: Session): ...

    @abstractmethod
    def set_live_time_as_now(self):...

    @abstractmethod
    def set_alarm_code(self, alarm_code:int):...

    @abstractmethod
    def get_stream(self, sid: str): ...

    @abstractmethod
    def on_open_future(self, future: Function): ...

    @abstractmethod
    def do_open_future(self, is_ok: bool, e: Optional[Exception]): ...

