from .Message import Message
from ..Costants import Flag


class Frame:

    def __init__(self, flag: int, message: Message):
        self.flag = flag
        self.message = message

    def get_flag(self) -> int:
        return self.flag

    def get_message(self) -> Message:
        return self.message

    def __str__(self) -> str:
        return f"Frame{{flag={Flag.name(self.flag)}, message={self.message}}}"
