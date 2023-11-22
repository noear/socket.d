from .Message import Message
from ..Costants import Flag


class Frame:

    def __init__(self, flag: Flag, message: Message):
        self.flag = flag
        self.message = message

    def get_flag(self) -> Flag:
        return self.flag

    def get_message(self) -> Message:
        return self.message

    def __str__(self) -> str:
        return f"Frame{{flag={self.flag}, message={self.message}}}"
