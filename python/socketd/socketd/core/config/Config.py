from concurrent.futures import Executor

from socketd.transport.Codec import Codec
from ..Costants import Function


class Config:
    # 流ID大小限制
    MAX_SIZE_SID = 64
    # 主题大小限制
    MAX_SIZE_TOPIC = 512
    # 元信息串大小限制
    MAX_SIZE_META_STRING = 4096
    # 分片大小限制
    MAX_SIZE_FRAGMENT = 1024 * 1024 * 16

    THEAD_POOL_SIZE = 10

    def client_mode(self) -> bool:
        """
        返回一个布尔值，指示配置是否为客户端模式。
        """
        pass

    def get_schema(self) -> str:
        """
        返回协议架构。
        """
        pass

    def get_charset(self) -> str:
        """
        返回字符集。
        """
        pass

    def get_codec(self) -> Codec:
        """
        返回编解码器。
        """
        pass

    def get_id_generator(self) -> Function:
        """
        返回ID生成器。
        """
        pass

    def get_fragment_handler(self):
        """
        返回分片处理器。
        """
        pass

    def get_ssl_context(self):
        """
        返回_s_sL上下文。
        """
        pass

    def get_executor(self) -> Executor:
        """
        返回执行器（第一优先级，某些底层可能不支持）。
        """
        pass

    def get_core_threads(self) -> int:
        """
        返回核心线程数（第二优先级）。
        """
        pass

    def get_max_threads(self) -> int:
        """
        返回最大线程数。
        """
        pass

    def get_reply_timeout(self) -> int:
        """
        返回答复超时时间（单位：毫秒）。
        """
        pass

    def get_max_requests(self) -> int:
        """
        返回允许的最大请求数。
        """
        pass

    def get_max_udp_size(self) -> int:
        """
        返回允许的最大_uDP包大小。
        """
        pass

    def get_request_timeout(self) -> float:
        """获取请求超时（单位：毫秒）"""
        pass

    def get_stream_timeout(self) -> float:
        """获取消息流超时（单位：毫秒）"""
        pass

    def get_role_name(self) -> str:
        """获取角色名"""
        pass