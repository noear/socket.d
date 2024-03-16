import uuid

class StrUtil(object):
    @staticmethod
    def guid() -> str:
        return str(uuid.uuid4()).replace("-", "")


    @staticmethod
    def hash_code(txt:str)->int:
        hashCode = 0
        for char in txt:
            hashCode = (hashCode * 31 + ord(char)) & 0xffffffff  # unsigned
        if hashCode & 0x7fffffff:
            hashCode -= 0xffffffff  # make it signed
        return hashCode
