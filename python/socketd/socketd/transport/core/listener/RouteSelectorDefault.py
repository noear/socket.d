from typing import Dict

from socketd.transport.core import Listener
from socketd.transport.core.listener.RouteSelector import RouteSelector
from socketd.utils.MapUtils import MapUtils


class RouteSelectorDefault(RouteSelector):
    def __init__(self):
        self._map:Dict[str, Listener] = {}
    def select(self, route: str) -> Listener:
        return self._map.get(route)

    def put(self, route: str, target: Listener):
        self._map.put(route, target)

    def remove(self, route: str):
        MapUtils.remove(self._map, route)

    def size(self) -> int:
        return self._map.__len__()
