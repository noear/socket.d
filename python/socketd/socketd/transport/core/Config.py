from abc import ABC, abstractmethod
from concurrent.futures import Executor

from socketd.transport.core.Codec import Codec
from socketd.transport.core import FragmentHandler
from socketd.transport.stream.StreamManger import StreamManger


class Config(ABC):
    # 流ID大小限制

    @abstractmethod
    def client_mode(self) -> bool:
        """
        返回一个布尔值，指示配置是否为客户端模式。
        """
        ...

    @abstractmethod
    def is_serial_send(self) -> bool:
        ...

    @abstractmethod
    def is_nolock_send(self) -> bool:
        ...

    @abstractmethod
    def get_stream_manger(self) -> StreamManger:
        ...

    @abstractmethod
    def get_role_name(self) -> str:
        """获取角色名"""
        ...

    @abstractmethod
    def get_charset(self) -> str:
        """
        返回字符集。
        """
        ...

    @abstractmethod
    def get_codec(self) -> Codec:
        """
        返回编解码器。
        """
        ...

    @abstractmethod
    def gen_id(self) -> str:
        """
        生成id
        """
        ...

    @abstractmethod
    def get_fragment_handler(self) -> FragmentHandler.FragmentHandler:
        """
        返回分片处理器。
        """
        ...

    @abstractmethod
    def get_fragment_size(self) -> int:
        """获取分片大小"""
        ...

    @abstractmethod
    def get_ssl_context(self):
        """
        返回_s_sL上下文。
        """
        ...

    @abstractmethod
    def get_io_threads(self) -> int:
        """
        返回核心线程数（第二优先级）。
        """
        ...

    @abstractmethod
    def get_codec_threads(self) -> int:
        """
        返回最大线程数。
        """
        ...

    @abstractmethod
    def get_exchange_threads(self) -> int:
        ...

    @abstractmethod
    def get_exchange_executor(self) -> Executor:
        """
        返回执行器（第一优先级，某些底层可能不支持）。
        """
        ...

    @abstractmethod
    def get_read_buffer_size(self) -> int:
        """
        返回答复超时时间（单位：毫秒）。
        """
        ...

    @abstractmethod
    def get_write_buffer_size(self) -> int:
        """
        返回允许的最大请求数。
        """
        ...

    @abstractmethod
    def get_idle_timeout(self) -> float:
        """闲置超时"""
        ...

    @abstractmethod
    def get_request_timeout(self) -> float:
        """获取请求超时（单位：毫秒）"""
        ...

    @abstractmethod
    def get_stream_timeout(self) -> float:
        """获取消息流超时（单位：毫秒）"""
        ...

    @abstractmethod
    def get_logger_level(self) -> str:
        ...

    @abstractmethod
    def get_is_thread(self) -> bool:
        ...

    @abstractmethod
    def get_max_udp_size(self) -> int:
        """
        返回允许的最大_uDP包大小。
        """
        ...

    def is_use_subprotocols(self) -> bool:
        """
        是否使用子协议
        """
        ...