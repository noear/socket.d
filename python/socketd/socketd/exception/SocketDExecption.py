class SocketDException(RuntimeError):

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketDAlarmException(SocketDException):
    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketDChannelException(SocketDException):
    """ 通道"""

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketDCodecException(SocketDException):
    """ 编码"""

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketDConnectionException(SocketDException):
    """连接"""

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketDSizeLimitException(SocketDException):
    """ 超长"""

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketDTimeoutException(SocketDException):
    """ 超时"""

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message
