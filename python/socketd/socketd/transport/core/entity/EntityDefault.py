import os

from io import BytesIO, TextIOWrapper, BufferedReader
from typing import Any

from socketd.transport.core.Entity import Entity
from socketd.transport.core.Costants import Constants
from loguru import logger


class EntityDefault(Entity):
    def __init__(self):
        self.meta_map = None
        self.meta_string = Constants.DEF_META_STRING
        self.meta_stringChanged = False
        self.data: BytesIO | TextIOWrapper = Constants.DEF_DATA
        self.data_size = 0

    def set_meta_string(self, meta_string):
        self.meta_map = None
        self.meta_string = meta_string
        self.meta_stringChanged = False
        return self

    def get_meta_string(self):
        if self.meta_stringChanged:
            buf = ""
            for name, val in self.get_meta_map().items():
                buf += f"{name}={val}&"
            if len(buf) > 0:
                buf = buf[:-1]
            self.meta_string = buf
            self.meta_stringChanged = False
        return self.meta_string

    def set_meta_map(self, meta_map):
        self.meta_map = meta_map
        self.meta_string = None
        self.meta_stringChanged = True
        return self

    def get_meta_map(self):
        if self.meta_map is None:
            self.meta_map = {}
            self.meta_stringChanged = False
            if self.meta_string:
                for kv_str in self.meta_string.split("&"):
                    kv = kv_str.split("=")
                    if len(kv) > 1:
                        self.meta_map[kv[0]] = kv[1]
                    else:
                        self.meta_map[kv[0]] = ""
        return self.meta_map

    def set_meta(self, name, val):
        self.put_meta(name, val)
        return self

    def put_meta(self, name, val):
        self.get_meta_map()[name] = val
        self.meta_stringChanged = True

    def get_meta(self, name) -> Any:
        return self.get_meta_map().get(name)

    def get_meta_or_default(self, name, default_val):
        if data := self.get_meta_map().get(name):
            return data
        return default_val

    def set_data(self, data: bytes | bytearray | memoryview | BytesIO | BufferedReader):
        _type = type(data)
        if _type == BytesIO:
            self.data = data
            self.data_size = len(data.getvalue())
        elif _type == BufferedReader:
            self.data = data
            self.data_size = data.seek(0, os.SEEK_END)
            data.seek(0)
        else:
            self.data = BytesIO(data)
            self.data_size = len(data)
        return self

    def get_data(self):
        return self.data

    def get_data_as_string(self):
        return self.data.getvalue().decode('utf-8')  # _assuming data is of type bytes

    def get_data_as_bytes(self) -> bytes:
        return self.data.getvalue()

    def get_data_size(self):
        return self.data_size

    def __str__(self):
        return f"Entity(meta='{self.get_meta_string()}', data=byte[{self.data_size}])"
