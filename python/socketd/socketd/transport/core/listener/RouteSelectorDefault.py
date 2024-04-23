from typing import Dict

from socketd.transport.core import Listener
from socketd.transport.core.listener.RouteSelector import RouteSelector


class RouteSelectorDefault(RouteSelector):
    def __init__(self):
        __inner:Dict[str, Listener] = {}
    def select(self, route: str) -> Listener:
        return self.__inner.get(route)

    def put(self, route: str, target: Listener):
        self.__inner.put(route, target)

    def remove(self, route: str):
        self.__inner.pop(route)

    def size(self) -> int:
        return self.__inner.__len__()
