from typing import Dict, Optional

import socketd.cluster.ClusterClient as ClusterClient
from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.client.Client import Client
from socketd.transport.client.ClientProvider import ClientProvider
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.server.ServerProvider import ServerProvider

from socketd_websocket.WsAioProvider import WsAioProvider
from socketd_aio_tcp.TcpAioProvider import TcpAioProvider


def version() -> str:
    return "2.4.8"


def protocol_version() -> str:
    return "1.0"


client_factory_map: Dict[str, ClientProvider] = {}
server_factory_map: Dict[str, ServerProvider] = {}


def load_factories(factories: list[ClientProvider | ServerProvider], factory_map: Dict[str, object]) -> None:
    for factory in factories:
        for schema in factory.schema():
            factory_map[schema] = factory


def __get_schema(url: str) -> Optional[str]:
    index = url.index("://")
    if index <= 0:
        raise SocketDException(f"The serverUrl invalid: {url}")
    return url[:index]


def create_server(server_config: ServerConfig) -> Server:
    factory = server_factory_map.get(server_config.get_schema())
    if factory is None:
        raise RuntimeError(f"No ServerBroker providers were found. {server_config.get_schema()}")
    return factory.create_server(server_config)


def create_client(server_url: str) -> Client:
    index = server_url.index("://")
    if index <= 0:
        raise SocketDException(f"The serverUrl invalid: {server_url}")
    schema = server_url[:index]
    if schema is None:
        raise ValueError("Invalid server URL.")

    client_config = ClientConfig(server_url[4:])
    factory = client_factory_map.get(schema)
    if factory is None:
        raise RuntimeError(f"No ClientBroker providers were found. {client_config.get_schema()}")
    return factory.create_client(client_config)


def create_cluster_client(*urls):
    return ClusterClient.ClusterClient(*urls)


# Initialize the client and server factory maps
load_factories([WsAioProvider(), TcpAioProvider()], server_factory_map)
load_factories([WsAioProvider(), TcpAioProvider()], client_factory_map)
