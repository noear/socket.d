from io import BytesIO
from typing import Optional, Any, Dict

from socketd.transport.core.Entity import Entity
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Message import MessageInternal


class MessageDefault(MessageInternal):
    def __init__(self, flag:int, sid:str, event:str, entity:Entity):
        self._flag = flag
        self._sid = sid
        self._event = event
        self._entity = entity

    def flag(self):
        return self._flag

    def is_end(self):
        return self._flag == Flags.ReplyEnd

    def is_request(self):
        return self._flag == Flags.Request

    def is_subscribe(self):
        return self._flag == Flags.Subscribe

    def is_close(self):
        return self._flag == Flags.Close

    def sid(self):
        return self._sid

    def event(self):
        return self._event

    def entity(self):
        return self._entity

    def __str__(self):
        return f"Message{{sid='{self._sid}', event='{self._event}', entity={self._entity}}}"

    def meta_string(self) -> str:
        return self._entity.meta_string()

    def meta_map(self) -> Dict[str, str]:
        return self._entity.meta_map()

    def meta(self, name: str) -> Optional[Any]:
        return self._entity.meta(name)

    def meta_or_default(self, name: str, default: str) -> str:
        return self._entity.meta_or_default(name, default)

    def meta_as_int(self, name:str) ->int:
        return self._entity.meta_as_int(name)

    def put_meta(self, name:str, val:str):
        return self._entity.put_meta(name, val)

    def del_meta(self, name:str):
        return self._entity.del_meta(name)

    def data(self) -> BytesIO:
        return self._entity.data()

    def data_as_string(self) -> str:
        return self._entity.data_as_string()


    def data_as_bytes(self) -> bytes:
        return self._entity.data_as_bytes()

    def data_size(self) -> int:
        return self._entity.data_size()

    def release(self):
        if self._entity:
            self._entity.release()

    def __str__(self):
        return f"Message(sid='{self._sid}', event='{self._event}', entity='{self._entity}')"


