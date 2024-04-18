from socketd.transport.core.impl.ConfigBase import ConfigBase


class ServerConfig(ConfigBase):
    def __init__(self, schema):
        super().__init__(False)

        self.__schema = schema

        if schema.startswith("sd:"):
            schema = schema[3:]

        self.__schemaCleaned = schema;
        self.__host = ""
        self.__port = 8602

    def get_schema(self):
        return self.__schema

    def get_host(self) -> str:
        return self.__host

    def host(self, host):
        self.__host = host
        return self;

    def get_port(self) -> int:
        return self.__port

    def port(self, port):
        self.__port = port
        return self

    def get_local_url(self) ->str:
        if self.__host:
            return f"sd:{self.__schemaCleaned}://{self.__host}:{self.__port}"
        else:
            return f"sd:{self.__schemaCleaned}://127.0.0.1:{self.__port}"

    def __str__(self):
        return f"ServerConfig{{schema='{self.__schemaCleaned}', " \
               f"charset='{self._charset}', "\
               f"host='{self.__host}', " \
               f"port={self.__port}, " \
               f"ioThreads='{self._ioThreads}', " \
               f"codecThreads={self._codecThreads}, " \
               f"exchangeThreads={self._exchangeThreads}, " \
               f"idleTimeout={self._idleTimeout}, "\
               f"requestTimeout={self._requestTimeout}, " \
               f"streamTimeout={self._streamTimeout}, " \
               f"readBufferSize={self.__read_buffer_size}, " \
               f"writeBufferSize={self.__write_buffer_size}, " \
               f"maxUdpSize={self._maxUdpSize}}}"
