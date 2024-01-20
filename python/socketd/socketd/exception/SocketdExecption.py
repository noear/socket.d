
class SocketdException(RuntimeError):

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class SocketdAlarmException(SocketdException):
    pass

class SocketdChannelException(SocketdException):
    pass

class SocketdCodecException(SocketdException):
    pass

class SocketdConnectionException(SocketdException):
    pass

class SocketdSizeLimitException(SocketdException):
    pass

class SocketdTimeoutException(SocketdException):
    pass
