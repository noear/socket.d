from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.client.Client import Client
from socketd.transport.client.ClientFactory import ClientFactory
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerFactory import ServerFactory

from .TCPAIOServer import TCPAIOServer
from .TcpAioClient import TcpAioClient


class TcpAioFactory(ClientFactory, ServerFactory):

    def schema(self) -> list[str]:
        return ["tcp", "tcp-python", "std:tcp"]

    def create_server(self, serverConfig: ServerConfig) -> Server:
        return TCPAIOServer(serverConfig)

    def create_client(self, clientConfig: ClientConfig) -> Client:
        return TcpAioClient(clientConfig)
