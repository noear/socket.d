from abc import ABC
from typing import Any, Dict
from socketd.transport.core.Session import Session
from socketd.transport.core.Channel import Channel


class SessionBase(Session, ABC):
    def __init__(self, channel: Channel):
        self._channel = channel
        self._attr_map = None
        self._session_id = None

    def get_attr_map(self) -> Dict[str, Any]:
        if self._attr_map is None:
            self._attr_map = {}

        return self._attr_map

    def get_attr(self, name: str) -> Any:
        if self._attr_map is None:
            return None

        return self._attr_map.get(name)

    def get_attr_or_default(self, name: str, default: Any) -> Any:
        tmp = self.get_attr(name)
        if tmp is None:
            return default
        else:
            return tmp

    def set_attr(self, name: str, value: Any) -> None:
        if self._attr_map is None:
            self._attr_map = {}
        self._attr_map[name] = value

    def get_session_id(self) -> str:
        if self._session_id is None:
            self._session_id = self.generate_id()

        return self._session_id

    def generate_id(self) -> str:
        return self._channel.get_config().get_id_generator()().__str__()

    def set_session_id(self, value):
        self._session_id = value
