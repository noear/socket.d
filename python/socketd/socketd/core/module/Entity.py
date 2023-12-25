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


class EntityMetas:
    META_SOCKETD_VERSION = "SocketD-Version"
    META_DATA_LENGTH = "Data-Length"
    META_DATA_FRAGMENT_IDX = "Data-Fragment-Idx"
    META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename"


class Reply:

    def is_end(self) -> bool: ...
