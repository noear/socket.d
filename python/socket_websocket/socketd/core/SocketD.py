from typing import Dict, Optional
from urllib.parse import urlparse

from socketd.core.config.ClientConfig import ClientConfig
from socketd.core.config.ServerConfig import ServerConfig
from socketd.transport.client.Client import Client
from socketd.transport.client.ClientFactory import ClientFactory
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerFactory import ServerFactory
from socketd_websocket.WsAioFactoy import WsAioFactory


class SocketD:
    @staticmethod
    def version() -> str:
        return "0.1"

    client_factory_map: Dict[str, ClientFactory] = {}
    server_factory_map: Dict[str, ServerFactory] = {}

    @staticmethod
    def load_factories(factories: list[ClientFactory | ServerFactory], factory_map: Dict[str, object]) -> None:
        for factory in factories:
            for schema in factory.schema():
                factory_map[schema] = factory

    @staticmethod
    def __get_schema(url: str) -> Optional[str]:
        parsed_url = urlparse(url)
        if parsed_url:
            return parsed_url.scheme
        return None

    @staticmethod
    def create_server(server_config: ServerConfig) -> Server:
        factory = SocketD.server_factory_map.get(server_config.schema)
        if factory is None:
            raise RuntimeError("No ServerBroker providers were found.")
        return factory.create_server(server_config)

    @staticmethod
    def create_client(server_url: str) -> Client:
        schema = SocketD.__get_schema(server_url)
        if schema is None:
            raise ValueError("Invalid server URL.")

        client_config = ClientConfig(server_url)
        factory = SocketD.client_factory_map.get(client_config.schema)
        if factory is None:
            raise RuntimeError("No ClientBroker providers were found.")
        return factory.create_client(client_config)


# Initialize the client and server factory maps
SocketD.load_factories([WsAioFactory()], SocketD.server_factory_map)
SocketD.load_factories([WsAioFactory()], SocketD.client_factory_map)
