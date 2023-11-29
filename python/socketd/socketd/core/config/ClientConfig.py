from urllib.parse import urlparse
from .ConfigBase import ConfigBase


class ClientConfig(ConfigBase):
    def __init__(self, url):
        super().__init__(True)
        self.url = url
        self.uri = urlparse(url)
        self.port = self.uri.port
        self.schema = self.uri.scheme

        if self.port is None:
            self.port = 8602

        self.connect_timeout = 3000
        self.heartbeat_interval = 20 * 1000
        self.auto_reconnect = True
        self.read_buffer_size = None
        self.write_buffer_size = None

    def get_schema(self):
        return self.schema

    def get_url(self):
        return self.url

    def get_uri(self):
        return self.uri

    def get_host(self):
        return self.uri.hostname

    def get_port(self):
        return self.port

    def get_heartbeat_interval(self):
        return self.heartbeat_interval

    def heartbeat_interval(self, heartbeat_interval):
        self.heartbeat_interval = heartbeat_interval
        return self

    def get_connect_timeout(self):
        return self.connect_timeout

    def connect_timeout(self, connect_timeout):
        self.connect_timeout = connect_timeout
        return self

    def get_read_buffer_size(self):
        return self.read_buffer_size

    def read_buffer_size(self, read_buffer_size):
        self.read_buffer_size = read_buffer_size
        return self

    def get_write_buffer_size(self):
        return self.write_buffer_size

    def write_buffer_size(self, write_buffer_size):
        self.write_buffer_size = write_buffer_size
        return self

    def is_auto_reconnect(self):
        return self.auto_reconnect

    def auto_reconnect(self, auto_reconnect):
        self.auto_reconnect = auto_reconnect
        return self

    def __str__(self):
        return f"ClientConfig{{schema='{self.schema}', url='{self.url}', " \
               f"heartbeatInterval={self.heartbeat_interval}, " \
               f"connectTimeout={self.connect_timeout}, " \
               f"readBufferSize={self.read_buffer_size}, " \
               f"writeBufferSize={self.write_buffer_size}, " \
               f"autoReconnect={self.auto_reconnect}, " \
               f"maxRequests={self.maxRequests}, " \
               f"maxUdpSize={self.maxUdpSize}}}"