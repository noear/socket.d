from abc import ABC

from .EntityDefault import EntityDefault


class StringEntity(EntityDefault, ABC):

    def __init__(self, string: str):
        super().__init__()
        self.data_set(string.encode("utf-8"))
