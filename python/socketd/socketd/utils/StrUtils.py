import uuid


class StrUtils(object):
    @staticmethod
    def guid() -> str:
        return str(uuid.uuid4()).replace("-", "")

    @staticmethod
    def is_empty(txt: str) -> bool:
        return not bool(txt)

    @staticmethod
    def is_not_empty(txt: str) -> bool:
        return not StrUtils.is_empty(txt)

    @staticmethod
    def hash_code(txt: str) -> int:
        hashCode = 0
        if StrUtils.is_empty(txt):
            return hashCode
        for char in txt:
            hashCode = (hashCode * 31 + ord(char)) & 0xffffffff  # unsigned
        if hashCode & 0x7fffffff:
            hashCode -= 0xffffffff  # make it signed
        return hashCode
