from socketd.core.config.ClientConfig import ClientConfig
from socketd.core.config.ServerConfig import ServerConfig
from socketd.transport.client.Client import Client
from socketd.transport.client.ClientFactory import ClientFactory
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerFactory import ServerFactory
from socketd_websocket.WsAioClient import WsAioClient
from socketd_websocket.WsAioServer import WsAioServer


class WsAioFactory(ClientFactory, ServerFactory):

    def schema(self) -> list[str]:
        return ["ws", "wss", "ws-python"]

    def create_server(self, serverConfig: ServerConfig) -> Server:
        return WsAioServer(serverConfig)

    def create_client(self, clientConfig: ClientConfig) -> Client:
        return WsAioClient(clientConfig)
