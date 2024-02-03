import os
import ssl
from abc import ABC

from typing import Callable, Any, Optional
from concurrent.futures import ThreadPoolExecutor

from socketd.transport.core.Config import Config
from socketd.transport.core.Codec import Codec
from socketd.transport.core.codec.CodecByteBuffer import CodecByteBuffer
from socketd.transport.core.Costants import Constants
from socketd.transport.core.fragment.FragmentHandlerDefault import FragmentHandlerDefault

from socketd.transport.core.config.logConfig import logger
from socketd.transport.stream.StreamManger import StreamManger
from socketd.transport.stream.StreamMangerDefault import StreamMangerDefault


class ConfigBase(Config, ABC):

    def __init__(self, client_mode: bool):
        self._client_mode = client_mode
        self._streamManger: Optional[StreamManger] = StreamMangerDefault(self)
        self._codec: Codec = CodecByteBuffer(self)

        self._charset = "utf-8"

        self._idGenerator: Optional[Callable] = None
        self._fragmentHandler: FragmentHandlerDefault = FragmentHandlerDefault()
        self._fragment_size = Constants.MAX_SIZE_FRAGMENT

        self._core_threads = os.cpu_count() * 2
        self._max_threads = self._core_threads * 4

        self._read_buffer_size = 512
        self._write_buffer_size = 512

        self._sslContext = None
        self._executor = None

        self._idle_timeout = 60_000
        self._request_timeout = 10_000
        self._stream_timeout = 1000 * 60 * 60 * 2

        self._max_requests = 10
        self._max_udp_size = 2048
        self.__is_thread = False

        self.__logger_level = "INFO"

    def client_mode(self):
        return self._client_mode

    def get_stream_manger(self) -> StreamManger:
        return self._streamManger

    def get_role_name(self) -> str:
        return "Client" if self.client_mode() else "Server"

    def get_charset(self):
        return self._charset

    def charset(self, charset):
        self._charset = charset
        return self

    def get_codec(self):
        return self._codec

    def codec(self, codec):
        assert codec is None
        self._codec = codec
        return self

    def get_fragment_handler(self):
        return self._fragmentHandler

    def fragment_handler(self, fragmentHandler):
        assert fragmentHandler is None
        self._fragmentHandler = fragmentHandler
        return self

    def get_id_generator(self) -> Callable[[], Any]:
        return self._idGenerator

    def id_generator(self, _idGenerator: Callable[[], Any]):
        # assert _idGenerator is None
        self._idGenerator = _idGenerator
        return self

    def get_ssl_context(self) -> ssl.SSLContext:
        return self._sslContext

    def ssl_context(self, ssl_context: ssl.SSLContext):
        return self

    def get_executor(self):
        if self._executor is None:
            __nThreads = self._core_threads if self.client_mode() else self._max_threads
            self._executor = ThreadPoolExecutor(max_workers=__nThreads, thread_name_prefix="socketd-channelExecutor-")
        return self._executor

    def executor(self, executor):
        self._executor = executor
        return self

    def get_core_threads(self):
        return self._core_threads

    def core_threads(self, core_threads):
        self._core_threads = core_threads
        return self

    def get_max_threads(self):
        return self._max_threads

    def max_threads(self, max_threads):
        self._max_threads = max_threads
        return self

    def get_read_buffer_size(self) -> int:
        return self._read_buffer_size

    def read_buffer_size(self, read_buffer_size):
        self._read_buffer_size = read_buffer_size
        return self

    def get_write_buffer_size(self) -> int:
        return self._write_buffer_size

    def write_buffer_size(self, write_buffer_size):
        self._write_buffer_size = write_buffer_size
        return self

    def max_udp_size(self, maxUdpSize):
        self._max_udp_size = maxUdpSize
        return self

    def get_request_timeout(self) -> float:
        return self._request_timeout

    def request_timeout(self, _request_time_out):
        self._request_timeout = _request_time_out
        return self

    def get_stream_timeout(self) -> float:
        return self._stream_timeout

    def stream_timeout(self, _stream_timeout):
        self._stream_timeout = _stream_timeout
        return self

    def is_thread(self, _is_thread):
        self.__is_thread = _is_thread
        return self

    def get_is_thread(self):
        return self.__is_thread

    def get_fragment_size(self) -> int:
        return self._fragment_size

    def get_idle_timeout(self) -> float:
        return self._idle_timeout

    def idle_timeout(self, _idle_timeout: float):
        self._idle_timeout = _idle_timeout
        return self

    def get_logger_level(self) -> str:
        return self.__logger_level

    def logger_level(self, __logger_level: str):
        self.__logger_level = __logger_level
        logger.setLevel(__logger_level)
        return self

    def get_max_udp_size(self) -> int:
        return self._max_udp_size
