from io import BufferedReader
from typing import BinaryIO

from socketd.exception.SocketDExecption import SocketDCodecException
from socketd.transport.core.Costants import EntityMetas
from socketd.transport.core.entity.EntityDefault import EntityDefault


class FileEntity(EntityDefault):

    def __init__(self, file: BufferedReader, filename: str):
        super().__init__()
        self._file: BufferedReader = file
        self.set_data(file)
        self.meta_put(EntityMetas.META_DATA_DISPOSITION_FILENAME, filename)

    def get_file(self) -> BinaryIO:
        return self._file

    def get_data_as_string(self):
        raise SocketDCodecException("FileEntity对象不允许转化为字符串")

    def get_data_as_bytes(self, n: int = -1):
        return self._file.read(n)

    def __str__(self):
        return self._file.__str__()

