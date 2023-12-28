from socketd.transport.core.entity.Entity import EntityMetas
from socketd.transport.core.entity.EntityDefault import EntityDefault


class FileEntity(EntityDefault):

    def __init__(self, byteIO: bytes, filename: str):
        super().__init__()
        self._file = byteIO
        self.set_data(byteIO)
        self.set_meta(EntityMetas.META_DATA_DISPOSITION_FILENAME, filename)
