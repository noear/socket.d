from socketd.transport.core.config.ConfigBase import ConfigBase


class ServerConfig(ConfigBase):
    def __init__(self, schema):
        super().__init__(False)
        self.__schema = schema
        self.__host = ""
        self.__port = 8602

    def get_schema(self):
        return self.__schema

    def get_host(self):
        return self.__host

    def host(self, host):
        self.__host = host
        return self;

    def get_port(self):
        return self.__port

    def port(self, port):
        self.__port = port
        return self

    def get_local_url(self):
        if self.__host:
            return f"sd:{self.__schema}://{self.__host}:{self.__port}"
        else:
            return f"sd:{self.__schema}://127.0.0.1:{self.__port}"

    def __str__(self):
        return f"ServerConfig{{schema='{self.__schema}', " \
               f"schema='{self.__schema}', " \
               f"charset='{self._charset}', "\
               f"host='{self.__host}', " \
               f"port={self.__port}, " \
               f"coreThreads='{self._coreThreads}', " \
               f"maxThreads={self._maxThreads}, " \
               f"idleTimeout={self._idle_timeout}, "\
               f"requestTimeout={self._request_timeout}, " \
               f"streamTimeout={self._stream_timeout}, " \
               f"readBufferSize={self.__read_buffer_size}, " \
               f"writeBufferSize={self.__write_buffer_size}, " \
               f"maxUdpSize={self._max_udp_size}}}"
