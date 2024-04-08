from urllib.parse import urlparse

from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.core.impl.ConfigBase import ConfigBase


class ClientConfig(ConfigBase):
    def __init__(self, url: str):
        super().__init__(True)

        idx = url.index("://")
        if idx < 2:
            raise SocketDException(f"The serverUrl invalid: {url}")

        self.__schema = url[:idx]

        if url.startswith("sd:"):
            url = url[3:]

        uri = urlparse(url)

        self.__linkUrl = "sd:" + url
        self.__url = url
        self.__host = uri.hostname
        self.__port = uri.port
        self.__schemaCleaned = uri.scheme

        if not self.__port:
            self.__port = 8602

        self.__connectTimeout = 10_000
        self.__heartbeatInterval = 20_000

        self.__autoReconnect = True

        self.__metaMap: dict[str, str] = dict()

    def get_link_url(self) -> str:
        return self.__linkUrl

    def get_url(self) -> str:
        return self.__url

    def get_schema(self) -> str:
        return self.__schema

    def get_host(self) -> str:
        return self.__host

    def get_port(self) -> int:
        return self.__port

    def get_meta_map(self) -> dict[str, str]:
        return self.__metaMap

    def meta_put(self, name, val):
        self.__metaMap[name] = val
        return self

    def get_heartbeat_interval(self) -> int:
        return self.__heartbeatInterval

    def heartbeat_interval(self, heartbeatInterval: int):
        self.__heartbeatInterval = heartbeatInterval
        return self

    def get_connect_timeout(self):
        return self.__connectTimeout

    def connect_timeout(self, connectTimeout):
        self.__connectTimeout = connectTimeout
        return self

    def is_auto_reconnect(self):
        return self.__autoReconnect

    def auto_reconnect(self, autoReconnect):
        self.__autoReconnect = autoReconnect
        return self

    def __str__(self):
        return f"ClientConfig{{schema='{self.__schemaCleaned}', " \
               f"charset='{self._charset}', " \
               f"url='{self.__url}', " \
               f"ioThreads={self._ioThreads}, " \
               f"codecThreads={self._codecThreads}, " \
               f"exchangeThreads={self._exchangeThreads}, " \
               f"heartbeatInterval={self.__heartbeatInterval}, " \
               f"connectTimeout={self.__connectTimeout}, " \
               f"idleTimeout={self._idleTimeout}, " \
               f"requestTimeout={self._requestTimeout}, " \
               f"streamTimeout={self._streamTimeout}, " \
               f"readBufferSize={self._readBufferSize}, " \
               f"writeBufferSize={self._writeBufferSize}, " \
               f"autoReconnect={self.__autoReconnect}, " \
               f"maxUdpSize={self._maxUdpSize}}}"
