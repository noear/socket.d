from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.server.Server import Server


class ServerProvider:

    def schema(self) -> list[str]: ...

    def create_server(self, serverConfig: ServerConfig) -> Server: ...
