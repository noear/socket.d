from abc import ABC
from typing import Any, Dict
from socketd.transport.core.Session import Session
from socketd.transport.core.Channel import Channel
from socketd.utils.MapUtils import MapUtils


class SessionBase(Session, ABC):
    def __init__(self, channel: Channel):
        self._channel = channel
        self._session_id = self.generate_id()
        self._attrMap:Dict[str, Any] = None

    def attr_map(self) -> Dict[str, Any]:
        if self._attrMap is None:
            self._attrMap = {}

        return self._attrMap

    def attr_has(self, name:str) -> bool:
        if self._attrMap is None:
            return False
        else:
            return self._attrMap.__contains__(name)

    def attr_del(self, name: str):
        if self._attrMap is not None:
            MapUtils.remove(self._attrMap, name)

    def attr(self, name: str) -> Any:
        if self._attrMap is None:
            return None

        return self._attrMap.get(name)

    def attr_or_default(self, name: str, defVal: Any) -> Any:
        tmp = self.attr(name)
        if tmp is None:
            return defVal
        else:
            return tmp

    def attr_put(self, name: str, val: Any) -> None:
        if self._attrMap is None:
            self._attrMap = {}
        self._attrMap[name] = val

    def session_id(self) -> str:
        return self._session_id

    def is_active(self) -> bool:
        return self.is_valid() and self.is_closing() == False

    def live_time(self)->int:
        return self._channel.get_live_time()

    def generate_id(self) -> str:
        return self._channel.get_config().gen_id()

