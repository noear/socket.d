from io import BytesIO


class Buffer(BytesIO):

    def __init__(self, limit,  *args, **kwargs):
        self.__limit = limit
        self.__size = 0
        super().__init__(*args, **kwargs)

    def flip(self):
        self.seek(0, 2)  # 将位置设置为末尾
        saved_data = self.getvalue()[::-1]  # 获取缓冲区中的数据
        self.truncate(0)  # 截断缓冲区为空
        self.seek(0)  # 将位置设置回起始位置
        super().write(saved_data)  # 将保存的数据写入缓冲区

    def remaining(self) -> int:
        if self.__size == 0:
            self.__size = len(super().getvalue())
        rem = self.__size - self.tell()
        return rem if rem > 0 else 0

    def position(self) -> int:
        ...

    def size(self):
        return self.__size

    def limit(self):
        return self.__limit

    def put_int(self, num: int):
        super().write(num.to_bytes(length=4, byteorder='big', signed=False))
        self.__size += 4

    def put_char(self, char:int):
        super().write(char.to_bytes(length=2, byteorder='big', signed=False))
        self.__size += 2

    def get_int(self):
        return int.from_bytes(self.read1(4), byteorder='big', signed=False)

    def write(self, __buffer) -> int:
        num = super().write(__buffer)
        self.__size += num
        return num


