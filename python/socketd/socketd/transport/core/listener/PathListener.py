from __future__ import annotations
from typing import Dict

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Session import Session
from socketd.transport.core.listener.PathMapper import PathMapperDefault


class PathListener(Listener):

    def __init__(self, mapper: PathMapperDefault):
        self._mapper: Dict[str, Listener] | PathMapperDefault = mapper

    def of(self, path: str, listener: Listener) -> PathListener:
        self._mapper[path] = listener
        return self

    async def on_open(self, session: Session):
        if l := self._mapper.get(session.path()):
            await l.on_open(session)

    async def on_message(self, session, message):
        if l := self._mapper.get(session.path()):
            await l.on_message(session)

    async def on_close(self, session):
        if l := self._mapper.get(session.path()):
            await l.on_close(session)

    def on_error(self, session, error):
        if l := self._mapper.get(session.path()):
            l.on_error(session)
