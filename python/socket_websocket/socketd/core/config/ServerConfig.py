from .ConfigBase import ConfigBase


class ServerConfig(ConfigBase):
    def __init__(self, schema):
        super().__init__(False)
        self.schema = schema
        self.host = ""
        self.port = 8602
        self.readBufferSize = 512
        self.writeBufferSize = 512

    def get_schema(self):
        return self.schema

    def getHost(self):
        return self.host

    def setHost(self, host):
        self.host = host

    def getPort(self):
        return self.port

    def setPort(self, port):
        self.port = port
        return self

    def getLocalUrl(self):
        if self.host:
            return f"{self.schema}://{self.host}:{self.port}"
        else:
            return f"{self.schema}://127.0.0.1:{self.port}"

    def getReadBufferSize(self):
        return self.readBufferSize

    def setReadBufferSize(self, readBufferSize):
        self.readBufferSize = readBufferSize

    def getWriteBufferSize(self):
        return self.writeBufferSize

    def setWriteBufferSize(self, writeBufferSize):
        self.writeBufferSize = writeBufferSize

    def __str__(self):
        return f"ServerConfig{{schema='{self.schema}', host='{self.host}', port={self.port}, readBufferSize={self.readBufferSize}, writeBufferSize={self.writeBufferSize}}}"
