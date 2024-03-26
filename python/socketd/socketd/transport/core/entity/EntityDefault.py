import os

from io import BytesIO, TextIOWrapper, BufferedReader
from typing import Any, Optional, BinaryIO

from socketd.transport.core.Entity import Entity
from socketd.transport.core.Costants import Constants
from socketd.transport.core.EntityMetas import EntityMetas


class EntityDefault(Entity):
    def __init__(self):
        self._meta_map: Optional[dict] = None
        self._meta_string = Constants.DEF_META_STRING
        self._meta_stringChanged = False
        self._data: BytesIO | TextIOWrapper = Constants.DEF_DATA
        self._data_size = 0

    def at(self, name: str):
        self.meta_put("@", name)
        return self

    def range(self, start: int, stop: int):
        self.meta_put(EntityMetas.META_RANGE_START, start)
        self.meta_put(EntityMetas.META_RANGE_SIZE, stop)
        self._meta_stringChanged = True
        return self

    def meta_string_set(self, meta_string):
        self._meta_map = None
        self._meta_string = meta_string
        self._meta_stringChanged = False
        return self

    def meta_string(self):
        if self._meta_stringChanged:
            buf = ""
            for name, val in self.meta_map().items():
                buf += f"{name}={val}&"
            if len(buf) > 0:
                buf = buf[:-1]
            self._meta_string = buf
            self._meta_stringChanged = False
        return self._meta_string

    def meta_map_put(self, metaMap:dict[str,str]):
        if metaMap:
            self.meta_map().update(metaMap)
            self._meta_stringChanged = True
        return self

    def meta_map(self):
        if self._meta_map is None:
            self._meta_map = {}
            self._meta_stringChanged = False
            if self._meta_string:
                for kv_str in self._meta_string.split("&"):
                    kv = kv_str.split("=")
                    if len(kv) > 1:
                        self._meta_map[kv[0]] = kv[1]
                    else:
                        self._meta_map[kv[0]] = ""
        return self._meta_map

    def meta_put(self, name:str, val:str):
        if val:
            self.meta_map()[name] = val
        else:
            self.meta_map().pop(name)

        self._meta_stringChanged = True
        return self

    def meta_del(self, name:str):
        self.meta_map().pop(name)
        self._meta_stringChanged = True
        return self

    def meta(self, name:str) -> Any:
        return self.meta_map().get(name)

    def meta_or_default(self, name:str, default_val:str):
        if data := self.meta_map().get(name):
            return data
        return default_val

    def meta_as_int(self, name:str) ->int:
        return int(self.meta_or_default(name,"0"))

    def put_meta(self, name:str, val:str):
        self.meta_put(name,val)

    def del_meta(self, name:str):
        self.meta_del(name)

    def data_set(self, data: bytes | bytearray | memoryview | BytesIO | BinaryIO):
        _type = type(data)
        if _type == BytesIO:
            self._data = data
            self._data_size = len(data.getvalue())
        elif _type == BufferedReader:
            self._data = data
            self._data_size = data.seek(0, os.SEEK_END)
            data.seek(0)
        else:
            self._data = BytesIO(data)
            self._data_size = len(data)
        return self

    def data(self):
        return self._data

    def data_as_string(self):
        return self._data.getvalue().decode('utf-8')  # _assuming _data is of type bytes

    def data_as_bytes(self) -> bytes:
        return self._data.getvalue()

    def data_size(self):
        return self._data_size

    def release(self):
        ...

    def __str__(self):
        return f"Entity(meta='{self.meta_string()}', data=byte[{self._data_size}])"
