from abc import ABC, abstractmethod
from typing import Dict, Optional, Any
from io import BytesIO


class Entity:
    def meta_string(self) -> str:
        raise NotImplementedError

    def meta_map(self) -> Dict[str, str]:
        raise NotImplementedError

    def meta(self, name: str) -> Optional[Any]:
        raise NotImplementedError

    def meta_or_default(self, name: str, default: str) -> str:
        raise NotImplementedError

    def meta_as_int(self, name:str)->int:
        raise NotImplementedError

    def put_meta(self, name:str, val:str):
        raise NotImplementedError

    def del_meta(self, name:str):
        raise NotImplementedError

    def data(self) -> BytesIO:
        raise NotImplementedError

    def data_as_string(self) -> str:
        raise NotImplementedError

    def data_as_bytes(self) -> bytes:
        raise NotImplementedError

    def data_size(self) -> int:
        raise NotImplementedError

    def release(self):
        raise NotImplementedError

class Reply(Entity, ABC):
    @abstractmethod
    def is_end(self) -> bool:
        ...

    @abstractmethod
    def sid(self) -> str:
        ...
