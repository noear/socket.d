from __future__ import annotations

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Session import Session
from socketd.transport.core.listener.RouteSelector import RouteSelector
from socketd.transport.core.listener.RouteSelectorDefault import RouteSelectorDefault
from socketd.utils.RunUtils import RunUtils


class PathListener(Listener):
    def __init__(self):
        _pathRouteSelector:RouteSelector = RouteSelectorDefault()

    def doOf(self, path: str, listener: Listener) -> PathListener:
        self._pathRouteSelector.put(path, listener)
        return self

    async def on_open(self, session: Session):
        if l := self._pathRouteSelector.select(session.path()):
            await RunUtils.waitTry(l.on_open(session))

    async def on_message(self, session, message):
        if l := self._pathRouteSelector.select(session.path()):
            await RunUtils.waitTry(l.on_message(session, message))

    async def on_close(self, session):
        if l := self._pathRouteSelector.select(session.path()):
            await RunUtils.waitTry(l.on_close(session))

    async def on_error(self, session, error:Exception):
        if l := self._pathRouteSelector.select(session.path()):
            await RunUtils.waitTry(l.on_error(session, error))
