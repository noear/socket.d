import os
import ssl

from typing import Callable
from .Config import Config
from socketd.core.handler.FragmentHandlerDefault import FragmentHandlerDefault
from socketd.transport.Codec import Codec
from socketd.transport.CodecByteBuffer import CodecByteBuffer


class ConfigBase(Config):

    def __init__(self, client_mode: bool):
        self._client_mode = client_mode
        self._charset = "utf-8"
        self._codec: Codec = CodecByteBuffer(self)
        self._idGenerator: Callable = None
        self._fragmentHandler: FragmentHandlerDefault = FragmentHandlerDefault()
        self._sslContext = None
        self._executor = None
        self._core_threads = os.cpu_count() * 2
        self._max_threads = self._core_threads * 8
        self._reply_timeout = 3000
        self._max_requests = 10
        self._maxUdpSize = 2048

    def client_mode(self):
        return self._client_mode

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

    def get_id_generator(self):
        return self._idGenerator

    def id_generator(self, _idGenerator):
        # assert _idGenerator is None
        self._idGenerator = _idGenerator
        return self

    def get_ssl_context(self):
        return self._sslContext

    def ssl_context(self, localhost_pem: str):
        self._sslContext = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        self._sslContext.load_cert_chain(localhost_pem)
        return self

    def get_executor(self):
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

    def get_reply_timeout(self):
        return self._reply_timeout

    def reply_timeout(self, reply_timeout):
        self._reply_timeout = reply_timeout
        return self

    def get_max_requests(self):
        return self._max_requests

    def max_requests(self, max_requests):
        self._max_requests = max_requests
        return self

    def get_max_udp_size(self):
        return self._maxUdpSize

    def max_udp_size(self, maxUdpSize):
        self._maxUdpSize = maxUdpSize
        return self
