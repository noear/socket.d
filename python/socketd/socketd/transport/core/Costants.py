from io import BytesIO
from typing import Callable

Function = Callable


class Constants:
    DEF_SID = ""
    DEF_EVENT = ""
    DEF_META_STRING = ""
    DEF_DATA = BytesIO()

    # 默认端口
    DEF_PORT = 8602

    MAX_SIZE_SID = 64
    # 主题大小限制
    MAX_SIZE_EVENT = 512
    # 元信息串大小限制
    MAX_SIZE_META_STRING = 4096
    # 数据长度最大限制（也是分片长度最大限制）
    MAX_SIZE_DATA = 1024 * 1024 * 16  # 16m
    # 帧长度最大限制
    MAX_SIZE_FRAME = 1024 * 1024 * 17  # 17m
    # 分片长度最小限制
    MIN_FRAGMENT_SIZE = 1024  # 1k

    THEAD_POOL_SIZE = 10

    # 零需求
    DEMANDS_ZERO = 0
    # 单需求
    DEMANDS_SINGLE = 1
    # 多需求
    DEMANDS_MULTIPLE = 2

    # 因协议关闭开始（安全关闭）
    CLOSE1000_PROTOCOL_CLOSE_STARTING = 1000
    # 因协议指令关闭
    CLOSE1001_PROTOCOL_CLOSE = 1001
    # 因协议非法关闭
    CLOSE1002_PROTOCOL_ILLEGAL = 1002
    # 因异常关闭
    CLOSE2001_ERROR = 2001
    # 因重连关闭
    CLOSE2002_RECONNECT = 2002
    # 因连接断开
    CLOSE2003_DISCONNECTION = 2003
    # 因打开失败关闭
    CLOSE2008_OPEN_FAIL = 2008
    # 因用户主动关闭（不可再重连）
    CLOSE2009_USER = 2009
