class Asserts:
    @staticmethod
    def assert_size(name: str, size: int, limitSize: int) -> None:
        if size > limitSize:
            buf = f"This message {name} size is out of limit {limitSize} ({size})"
            raise RuntimeError(buf)