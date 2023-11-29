from socketd.core.config.ServerConfig import ServerConfig
from socketd.transport.server.Server import Server


class ServerFactory:

    def schema(self) -> list[str]: ...

    def create_server(self, serverConfig: ServerConfig) -> Server: ...
