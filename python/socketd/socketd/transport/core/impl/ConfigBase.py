import os
import ssl
from abc import ABC
from typing import Optional
from concurrent.futures import ThreadPoolExecutor

from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.core.Asserts import Asserts
from socketd.transport.core.Config import Config
from socketd.transport.core.Codec import Codec
from socketd.transport.core.IdGenerator import IdGenerator, GuidGenerator
from socketd.transport.core.codec.CodecDefault import CodecDefault
from socketd.transport.core.Costants import Constants
from socketd.transport.core.fragment.FragmentHandlerDefault import FragmentHandlerDefault

from socketd.utils.LogConfig import logger
from socketd.transport.stream.StreamManger import StreamManger
from socketd.transport.stream.StreamMangerDefault import StreamMangerDefault


class ConfigBase(Config, ABC):

    def __init__(self, clientMode: bool):
        self._clientMode = clientMode
        self._serialSend = False
        self._nolockSend = False

        self._streamManger: StreamManger = StreamMangerDefault(self)
        self._codec: Codec = CodecDefault(self)

        self._charset = "utf-8"

        self._idGenerator: type[GuidGenerator] = GuidGenerator
        self._fragmentHandler: FragmentHandlerDefault = FragmentHandlerDefault()
        self._fragment_size = Constants.MAX_SIZE_DATA

        self._ioThreads = 1
        self._codecThreads = os.cpu_count()
        self._exchangeThreads = os.cpu_count() * 4

        self._readBufferSize = 1024 * 4  # 4k
        self._writeBufferSize = 1024 * 4  # 4k

        self._sslContext = None
        self._exchangeExecutor: Optional[ThreadPoolExecutor] = None

        self._idleTimeout = 60_000  # 60秒（心跳默认为20秒）
        self._requestTimeout = 10_000  # 10秒（默认与连接超时同）
        self._streamTimeout = 1000 * 60 * 60 * 2  # 2小时 //避免永不回调时，不能释放

        self._maxUdpSize = 2048

        self.__isThread = False
        self.__loggerLevel = "INFO"

    def __del__(self):
        if self._exchangeExecutor:
            self._exchangeExecutor.shutdown()

    def client_mode(self):
        return self._clientMode

    def is_serial_send(self) -> bool:
        return self._serialSend

    def serial_send(self, serialSend: bool):
        self._serialSend = serialSend
        return self

    def is_nolock_send(self) -> bool:
        return self._nolockSend

    def nolock_send(self, nolockSend: bool):
        self._nolockSend = nolockSend
        return self

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

    def get_fragment_handler(self):
        return self._fragmentHandler

    def fragment_handler(self, fragmentHandler):
        Asserts.assert_null("fragmentHandler", fragmentHandler)

        self._fragmentHandler = fragmentHandler
        return self

    def get_fragment_size(self) -> int:
        return self._fragment_size

    def fragment_size(self, fragmentSize: int):
        if fragmentSize > Constants.MAX_SIZE_DATA:
            raise SocketDException("The parameter fragmentSize cannot > 16m")

        if fragmentSize < Constants.MIN_FRAGMENT_SIZE:
            raise SocketDException("The parameter fragmentSize cannot < 1k")

        self._fragment_size = fragmentSize
        return self

    def gen_id(self) -> str:
        return self._idGenerator()

    def id_generator(self, idGenerator: IdGenerator):
        Asserts.assert_null("idGenerator", idGenerator)

        self._idGenerator = idGenerator
        return self

    def get_ssl_context(self) -> ssl.SSLContext:
        return self._sslContext

    def ssl_context(self, sslContext: ssl.SSLContext):
        self._sslContext = sslContext
        return self

    def get_exchange_executor(self):
        if self._exchangeExecutor is None:
            __nThreads = self._exchangeThreads
            self._exchangeExecutor = ThreadPoolExecutor(max_workers=__nThreads,
                                                        thread_name_prefix="socketd-exchangeExecutor-")
        return self._exchangeExecutor

    def exchange_executor(self, exchangeExecutor):
        self._exchangeExecutor = exchangeExecutor
        return self

    def get_io_threads(self):
        return self._ioThreads

    def io_threads(self, ioThreads):
        self._ioThreads = ioThreads
        return self

    def get_codec_threads(self):
        return self._codecThreads

    def codec_threads(self, codecThreads):
        self._codecThreads = codecThreads
        return self

    def get_exchange_threads(self):
        return self._exchangeThreads

    def exchange_threads(self, exchangeThreads):
        self._exchangeThreads = exchangeThreads
        return self

    def get_read_buffer_size(self) -> int:
        return self._readBufferSize

    def read_buffer_size(self, read_buffer_size):
        self._readBufferSize = read_buffer_size
        return self

    def get_write_buffer_size(self) -> int:
        return self._writeBufferSize

    def write_buffer_size(self, write_buffer_size):
        self._writeBufferSize = write_buffer_size
        return self

    def get_idle_timeout(self) -> float:
        return self._idleTimeout

    def idle_timeout(self, _idle_timeout: float):
        self._idleTimeout = _idle_timeout
        return self

    def get_request_timeout(self) -> float:
        return self._requestTimeout

    def request_timeout(self, _request_time_out):
        self._requestTimeout = _request_time_out
        return self

    def get_stream_timeout(self) -> float:
        return self._streamTimeout

    def stream_timeout(self, _stream_timeout):
        self._streamTimeout = _stream_timeout
        return self

    def is_thread(self, _is_thread):
        self.__isThread = _is_thread
        return self

    def get_is_thread(self):
        return self.__isThread

    def get_logger_level(self) -> str:
        return self.__loggerLevel

    def logger_level(self, __logger_level: str):
        self.__loggerLevel = __logger_level
        logger.setLevel(__logger_level)
        return self

    def get_max_udp_size(self) -> int:
        return self._maxUdpSize

    def max_udp_size(self, maxUdpSize):
        self._maxUdpSize = maxUdpSize
        return self
