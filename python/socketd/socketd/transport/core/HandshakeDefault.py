from typing import Dict
from urllib.parse import urlparse, parse_qsl

from socketd.transport.core.EntityMetas import EntityMetas
from socketd.transport.core.Message import Message, MessageInternal
from socketd.utils.StrUtils import StrUtils


class Handshake:
    def version(self)->str:
        ...
    def uri(self):
        ...
    def path(self)->str:
        ...
    def param_map(self) -> Dict[str, str]:
        ...
    def param(self, name: str):
        ...
    def param_or_default(self, name:str, val:str):
        ...
    def param_put(self, name:str, val:str):
        ...
    def out_meta(self, name:str, val:str):
        ...


class HandshakeInternal(Handshake):
    def get_source(self) -> MessageInternal: ...
    def get_out_meta_map(self) -> dict[str,str]:...


class HandshakeDefault(HandshakeInternal):
    def __init__(self, source: MessageInternal):
        linkUrl = source.data_as_string()
        if StrUtils.is_empty(linkUrl):
            # 兼容旧版本（@deprecated 2.2）
            linkUrl = source.event()

        self._source: MessageInternal = source
        self._uri = urlparse(linkUrl)
        self._path = self._uri.path
        self._version = source.meta(EntityMetas.META_SOCKETD_VERSION)
        self._paramMap = self._parse_query_string(self._uri.query)
        self._outMetaMap:dict[str,str] = {}

        if StrUtils.is_empty(self._path):
            self._path = "/" # tcp://1.1.1.1 无路径连接时，path 为空

        self._paramMap.update(source.meta_map())

    def get_source(self) -> Message:
        return self._source

    def version(self):
        return self._version

    def uri(self):
        return self._uri

    def path(self):
        return self._path

    def param_map(self) -> Dict[str, str]:
        return self._paramMap

    def param(self, name: str):
        return self._paramMap.get(name)

    def param_or_default(self, name: str, defVal: str):
        if data := self._paramMap.get(name):
            return data
        else:
            return defVal
    def param_put(self, name, value):
        self._paramMap[name] = value

    def out_meta(self, name:str, val:str):
        self._outMetaMap[name] = val

    def get_out_meta_map(self) -> dict[str,str]:
        return self._outMetaMap

    @staticmethod
    def _parse_query_string(query_string):
        params = {}
        if StrUtils.is_not_empty(query_string):
            for name, value in parse_qsl(query_string):
                params[name] = value
        return params


