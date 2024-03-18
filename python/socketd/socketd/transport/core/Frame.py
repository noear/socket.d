from .Flags import Flags
from .Message import Message


class Frame:

    def __init__(self, flag: int, message: Message | None):
        self._flag = flag
        self._message = message

    def flag(self) -> int:
        return self._flag

    def message(self) -> Message:
        return self._message

    def __str__(self) -> str:
        return f"Frame{{flag={Flags.name(self._flag)}, message={self._message}}}"
