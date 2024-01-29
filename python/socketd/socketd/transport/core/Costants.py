from io import BytesIO
from typing import Callable

Function = Callable


class Flag:
    Unknown = 0
    Connect = 10
    Connack = 11
    Ping = 20
    Pong = 21
    Close = 30
    Alarm = 31
    Message = 40
    Request = 41
    Subscribe = 42
    Reply = 48
    ReplyEnd = 49

    @staticmethod
    def of(code):
        if code == 10:
            return Flag.Connect
        elif code == 11:
            return Flag.Connack
        elif code == 20:
            return Flag.Ping
        elif code == 21:
            return Flag.Pong
        elif code == 30:
            return Flag.Close
        elif code == 40:
            return Flag.Message
        elif code == 41:
            return Flag.Request
        elif code == 42:
            return Flag.Subscribe
        elif code == 48:
            return Flag.Reply
        elif code == 49:
            return Flag.ReplyEnd
        else:
            return Flag.Unknown

    @staticmethod
    def name(code):
        if code == 10:
            return "Connect"
        elif code == 11:
            return "Connack"
        elif code == 20:
            return "Ping"
        elif code == 21:
            return "Pong"
        elif code == 30:
            return "Close"
        elif code == 40:
            return "Message"
        elif code == 41:
            return "Request"
        elif code == 42:
            return "Subscribe"
        elif code == 48:
            return "Reply"
        elif code == 49:
            return "ReplyEnd"
        else:
            return "Unknown"


class Constants:
    DEF_SID = ""
    DEF_EVENT = ""
    DEF_META_STRING = ""
    DEF_DATA = BytesIO()

    MAX_SIZE_SID = 64
    # 主题大小限制
    MAX_SIZE_EVENT = 512
    # 元信息串大小限制
    MAX_SIZE_META_STRING = 4096
    # 分片大小限制
    MAX_SIZE_FRAGMENT = 1024 * 1024 * 16
    # 帧长度最大限制
    MAX_SIZE_FRAME = 1024 * 1024 * 17

    THEAD_POOL_SIZE = 10
    # 单需求
    DEMANDS_SINGLE = 1
    # 多需求
    DEMANDS_MULTIPLE = 2
    DEMANDS_ZERO = 0
    # 因异常关闭
    CLOSE3_ERROR = 3



class EntityMetas:
    # 框架版本号
    META_SOCKETD_VERSION = "SocketD"
    # 数据长度
    META_DATA_LENGTH = "Data-Length"
    # 数据类型
    META_DATA_TYPE = "Data-Type"
    # 数据分片索引
    META_DATA_FRAGMENT_IDX = "Data-Fragment-Idx"
    # 数据分片总数
    META_DATA_FRAGMENT_TOTAL = "Data-Fragment-Total"
    # 数据描述之文件名
    META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename"
    # 数据范围开始（相当于分页）
    META_RANGE_START = "Data-Range-Start"
    # 数据范围长度
    META_RANGE_SIZE = "Data-Range-Size"

