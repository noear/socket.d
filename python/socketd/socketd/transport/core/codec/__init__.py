__all__ = ["bytes_to_int32"]


def bytes_to_int32(_bytes: bytes) -> int:
    value = 0
    for i in range(4):
        shift = (3 - i) * 8
        value += (_bytes[i] & 0xFF) << shift
    return value
