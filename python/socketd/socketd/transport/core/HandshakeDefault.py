from typing import Dict
from urllib.parse import urlparse, parse_qsl

from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Message import Message, MessageInternal


class Handshake:

    def uri(self): ...

    def param_map(self) -> Dict[str, str]: ...

    def param(self, name: str): ...

    def param_or_default(self, name, value): ...

    def version(self): ...

    def param_put(self, name, value): ...


class HandshakeInternal(Handshake):
    def get_source(self) -> MessageInternal: ...
    def get_out_meta_map(self) -> dict[str,str]:...


class HandshakeDefault(HandshakeInternal):
    def __init__(self, message: MessageInternal):
        self._source: MessageInternal = message
        linkUrl = message.data_as_string()
        if not linkUrl:
            linkUrl = message.event()
        self._uri = urlparse(linkUrl)
        self._path = self._uri.path
        self._entity = message.entity()
        self._version = self._entity.meta(EntityMetas.META_SOCKETD_VERSION)
        self._param_map = self._parse_query_string(self._uri.query)
        self._out_meta_map:dict[str,str] = {}

    def version(self):
        return self._version

    def uri(self):
        return self._uri

    def path(self):
        return self._path

    def param_map(self) -> Dict[str, str]:
        return self._param_map

    def param(self, name: str):
        return self._param_map.get(name)

    def param_or_default(self, name: str, defVal: str):
        if data := self._param_map.get(name):
            return data
        else:
            return defVal
    def param_put(self, name, value):
        self._param_map[name] = value

    @staticmethod
    def _parse_query_string(query_string):
        params = {}
        if query_string:
            for name, value in parse_qsl(query_string):
                params[name] = value
        return params

    def get_source(self) -> Message:
        return self._source
    def get_out_meta_map(self) -> dict[str,str]:
        return self._out_meta_map


