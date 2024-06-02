from socketd.transport.client.ClientSession import ClientSession

# 会话工具（主要检测状态）
class SessionUtils:
    @staticmethod
    def is_active(s: ClientSession) -> bool:
        return s and  s.is_active()

    @staticmethod
    def is_valid(s: ClientSession) -> bool:
        return s and s.is_valid()