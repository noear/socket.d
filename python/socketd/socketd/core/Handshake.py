from typing import Dict
from urllib.parse import urlparse, parse_qsl

from .module.Entity import EntityMetas
from .module.Message import Message


class Handshake:
    def __init__(self, message: Message):
        self.uri = urlparse(message.get_topic())
        self.entity = message.get_entity()
        self.version = self.entity.get_meta(EntityMetas.META_SOCKETD_VERSION)
        self.param_map = self._parse_query_string(self.uri.query)

    def get_uri(self):
        return self.uri

    def get_param_map(self) -> Dict[str, str]:
        return self.param_map

    def get_param(self, name: str):
        return self.param_map.get(name)

    def get_version(self):
        return self.version

    @staticmethod
    def _parse_query_string(query_string):
        params = {}
        if query_string:
            for name, value in parse_qsl(query_string):
                params[name] = value
        return params
