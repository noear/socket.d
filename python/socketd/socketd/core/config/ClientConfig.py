from urllib.parse import urlparse
from .ConfigBase import ConfigBase


class ClientConfig(ConfigBase):
    def __init__(self, __url):
        super().__init__(True)
        self.__url = __url
        self.__uri = urlparse(__url)
        self.__port = self.__uri.port
        self.__schema = self.__uri.scheme

        if self.__port is None:
            self.__port = 8602

        self.__connect_timeout = 3000
        self.__heartbeat_interval = 20 * 1000
        self.__auto_reconnect = True
        self.__read_buffer_size = None
        self.__write_buffer_size = None

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

    def heartbeat_interval(self, __heartbeat_interval):
        self.__heartbeat_interval = __heartbeat_interval
        return self

    def get_connect_timeout(self):
        return self.__connect_timeout

    def connect_timeout(self, __connect_timeout):
        self.__connect_timeout = __connect_timeout
        return self

    def get_read_buffer_size(self):
        return self.__read_buffer_size

    def read_buffer_size(self, __read_buffer_size):
        self.__read_buffer_size = __read_buffer_size
        return self

    def get_write_buffer_size(self):
        return self.__write_buffer_size

    def write_buffer_size(self, __write_buffer_size):
        self.__write_buffer_size = __write_buffer_size
        return self

    def is_auto_reconnect(self):
        return self.__auto_reconnect

    def auto_reconnect(self, __auto_reconnect):
        self.__auto_reconnect = __auto_reconnect
        return self



    def __str__(self):
        return f"ClientConfig{{__schema='{self.__schema}', __url='{self.__url}', " \
               f"heartbeatInterval={self.__heartbeat_interval}, " \
               f"connectTimeout={self.__connect_timeout}, " \
               f"readBufferSize={self.__read_buffer_size}, " \
               f"writeBufferSize={self.__write_buffer_size}, " \
               f"autoReconnect={self.__auto_reconnect}, " \
               f"maxRequests={self._max_requests}, " \
               f"maxUdpSize={self._max_udp_size}}}"
