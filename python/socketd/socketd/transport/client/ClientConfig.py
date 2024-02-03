from urllib.parse import urlparse
from socketd.transport.core.config.ConfigBase import ConfigBase


class ClientConfig(ConfigBase):
    def __init__(self, url: str):
        super().__init__(True)

        if url.startswith("sd:"):
            url = url[3:]

        self.__url = url
        self.__uri = urlparse(url)
        self.__port = self.__uri.port
        self.__schema = self.__uri.scheme
        self.__link_uri = "sd:" + url

        if self.__port is None:
            self.__port = 8602

        self.__connect_timeout = 10_000
        self.__heartbeat_interval = 20_000
        self.__auto_reconnect = True

        self.__meta = dict()

    def get_schema(self):
        return self.__schema

    def get_url(self):
        return self.__url

    def get_uri(self):
        return self.__uri

    def get_host(self):
        return self.__uri.hostname

    def get_port(self):
        return self.__port

    def get_heartbeat_interval(self):
        return self.__heartbeat_interval

    def heartbeat_interval(self, __heartbeat_interval: float):
        self.__heartbeat_interval = __heartbeat_interval
        return self

    def get_connect_timeout(self):
        return self.__connect_timeout

    def connect_timeout(self, __connect_timeout):
        self.__connect_timeout = __connect_timeout
        return self

    def is_auto_reconnect(self):
        return self.__auto_reconnect

    def auto_reconnect(self, __auto_reconnect):
        self.__auto_reconnect = __auto_reconnect
        return self

    def get_link_url(self):
        return self.__link_uri

    def get_meta(self):
        return self.__meta

    def meta_put(self, name, val):
        self.__meta[name] = val
        return self

    def __str__(self):
        return f"ClientConfig{{schema='{self.__schema}', " \
               f"charset='{self._charset}', " \
               f"url='{self.__url}', " \
               f"heartbeatInterval={self.__heartbeat_interval}, " \
               f"connectTimeout={self.__connect_timeout}, " \
               f"idleTimeout={self._idle_timeout}, " \
               f"requestTimeout={self._request_timeout}, " \
               f"streamTimeout={self._stream_timeout}, " \
               f"readBufferSize={self._read_buffer_size}, " \
               f"writeBufferSize={self._write_buffer_size}, " \
               f"autoReconnect={self.__auto_reconnect}, " \
               f"maxUdpSize={self._max_udp_size}}}"
