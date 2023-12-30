from socketd.transport.server.Server import Server
from socketd.transport.server.ServerConfig import ServerConfig


class ServerFactory:

    def schema(self) -> list[str]: ...

    def create_server(self, serverConfig: ServerConfig) -> Server: ...
