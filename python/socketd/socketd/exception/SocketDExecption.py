from socketd.transport.core.Message import Message


class SocketDException(RuntimeError):

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketDAlarmException(SocketDException):
    """ 告警"""
    def __init__(self, alarm:Message):
        super().__init__(alarm.data_as_string())
        self.__alarm = alarm
    def get_alarm(self):
        return self.__alarm


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
