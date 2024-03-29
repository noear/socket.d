class SocketDException(RuntimeError):

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketDAlarmException(SocketDException):
    """ 告警"""
    pass


class SocketDChannelException(SocketDException):
    """ 通道"""
    pass


class SocketDCodecException(SocketDException):
    """ 编码"""
    pass


class SocketDConnectionException(SocketDException):
    """连接"""
    pass


class SocketDSizeLimitException(SocketDException):
    """ 超长"""
    pass


class SocketDTimeoutException(SocketDException):
    """ 超时"""
    pass
