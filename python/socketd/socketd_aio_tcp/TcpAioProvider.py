from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.client.ClientProvider import ClientProvider
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.client.Client import Client
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerProvider import ServerProvider
from socketd_aio_tcp import TCPAIOServer, TcpAioClient


class TcpAioProvider(ClientProvider, ServerProvider):

    def schema(self) -> list[str]:
        return ["tcp", "tcp-python", "sd:tcp"]

    def create_server(self, serverConfig: ServerConfig) -> Server:
        return TCPAIOServer(serverConfig)

    def create_client(self, clientConfig: ClientConfig) -> Client:
        return TcpAioClient(clientConfig)
