from io import BytesIO


class Buffer(BytesIO):

    def __init__(self,  *args, **kwargs):
        self.__limit = kwargs.get("limit")
        if self.__limit is not None:
            kwargs.pop("limit")
        super().__init__(*args, **kwargs)

    def flip(self):
        self.seek(0, 2)  # 将位置设置为末尾
        saved_data = self.getvalue()[::-1]  # 获取缓冲区中的数据
        self.truncate(0)  # 截断缓冲区为空
        self.seek(0)  # 将位置设置回起始位置
        self.write(saved_data)  # 将保存的数据写入缓冲区

    def remaining(self):
        remaining_data = self.getbuffer()[self.tell():]  # 获取剩余的字节数据
        remaining_length = len(remaining_data)  # 获取剩余字节数据的长度
        return remaining_length

    def limit(self):
        return self.__limit

    def size(self):
        return len(self.getbuffer())

    def put_int(self, num: int):
        self.write(num.to_bytes(length=4, byteorder='little', signed=False))

    def get_int(self):
        return int.from_bytes(self.read1(4), byteorder='little', signed=False)

