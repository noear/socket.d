from abc import ABC
from typing import Dict, Optional, Any
from io import BytesIO


class Entity:
    def get_meta_string(self) -> str:
        raise NotImplementedError

    def get_meta_map(self) -> Dict[str, str]:
        raise NotImplementedError

    def get_meta(self, name: str) -> Optional[Any]:
        raise NotImplementedError

    def get_meta_or_default(self, name: str, default: str) -> str:
        raise NotImplementedError

    def get_data(self) -> BytesIO:
        raise NotImplementedError

    def get_data_as_string(self) -> str:
        raise NotImplementedError

    def get_data_size(self) -> int:
        raise NotImplementedError

    def get_data_as_bytes(self) -> bytes:
        raise NotImplementedError


class Reply(Entity, ABC):

    def is_end(self) -> bool: ...

    def get_sid(self): ...
