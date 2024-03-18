from __future__ import annotations

from socketd.transport.core import Entity
from socketd.transport.core.Costants import Constants
from socketd.transport.core.Flags import Flags
from socketd.transport.core.entity.MessageDefault import MessageDefault


class MessageBuilder:
    def __init__(self):
        self._flag = Flags.Unknown
        self._sid = Constants.DEF_SID
        self._event = Constants.DEF_EVENT
        self._entity = None

    def flag(self, flag: int):
        self._flag = flag
        return self

    def sid(self, sid: str):
        self._sid = sid
        return self

    def event(self, event: str):
        self._event = event
        return self

    def entity(self, entity: Entity):
        self._entity = entity
        return self

    def build(self) -> MessageDefault:
        return MessageDefault(self._flag, self._sid, self._event, self._entity)
