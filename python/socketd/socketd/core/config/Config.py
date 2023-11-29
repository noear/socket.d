from concurrent.futures import Executor

from socketd.core.ThreadSafeDict import ThreadSafeDict


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

    def client_mode(self):
        """
        返回一个布尔值，指示配置是否为客户端模式。
        """
        pass

    def get_schema(self):
        """
        返回协议架构。
        """
        pass

    def get_charset(self):
        """
        返回字符集。
        """
        pass

    def get_codec(self):
        """
        返回编解码器。
        """
        pass

    def get_id_generator(self):
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

    def get_core_threads(self):
        """
        返回核心线程数（第二优先级）。
        """
        pass

    def get_max_threads(self):
        """
        返回最大线程数。
        """
        pass

    def get_reply_timeout(self):
        """
        返回答复超时时间（单位：毫秒）。
        """
        pass

    def get_max_requests(self):
        """
        返回允许的最大请求数。
        """
        pass

    def get_max_udp_size(self):
        """
        返回允许的最大_uDP包大小。
        """
        pass

    def get_thread_local_map(self) -> ThreadSafeDict:
        pass