from .ConfigBase import ConfigBase


class ServerConfig(ConfigBase):
    def __init__(self, schema):
        super().__init__(False)
        self.__schema = schema
        self.__host = ""
        self.__port = 8602
        self.__read_buffer_size = 512
        self.__write_buffer_size = 512

    def get_schema(self):
        return self.__schema

    def get_host(self):
        return self.__host

    def set_host(self, host):
        self.__host = host

    def get_port(self):
        return self.__port

    def set_port(self, port):
        self.__port = port
        return self

    def get_local_url(self):
        if self.__host:
            return f"{self.__schema}://{self.__host}:{self.__port}"
        else:
            return f"{self.__schema}://127.0.0.1:{self.__port}"

    def get_read_buffer_size(self):
        return self.__read_buffer_size

    def set_read_buffer_size(self, _read_buffer_size):
        self.__read_buffer_size = _read_buffer_size

    def get_write_buffer_size(self):
        return self.__write_buffer_size

    def set_write_buffer_size(self, _write_buffer_size):
        self.__write_buffer_size = _write_buffer_size

    def __str__(self):
        return f"ServerConfig{{schema='{self.__schema}', host='{self.__host}', port={self.__port}, readBufferSize={self.__read_buffer_size}, writeBufferSize={self.__write_buffer_size}}}"
