from typing import Dict, Optional

import socketd.cluster.ClusterClient as ClusterClient
from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.client.ClientConfig import ClientConfig
from socketd.transport.client.Client import Client
from socketd.transport.client.ClientProvider import ClientProvider
from socketd.transport.core.Asserts import Asserts
from socketd.transport.server.Server import Server
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.server.ServerProvider import ServerProvider

from socketd_websocket.WsAioProvider import WsAioProvider
from socketd_aio_tcp.TcpAioProvider import TcpAioProvider


def version() -> str:
    return "2.4.9"


def protocol_version() -> str:
    return "1.0"


client_factory_map: Dict[str, ClientProvider] = {}
server_factory_map: Dict[str, ServerProvider] = {}


def load_factories(factories: list[ClientProvider | ServerProvider], factory_map: Dict[str, object]) -> None:
    for factory in factories:
        for schema in factory.schema():
            factory_map[schema] = factory
def create_server(schemaOrConfig: str | ServerConfig) -> Server:
    Asserts.assert_null("schemaOrConfig", schemaOrConfig)
    config:ServerConfig

    if isinstance(schemaOrConfig, str):
        config = ServerConfig(schemaOrConfig)
    else:
        config = schemaOrConfig

    server = create_server_or_null(config)

    if server is None:
        raise RuntimeError(f"No socketd server providers were found: {config.get_schema()}")
    else:
        return server

def create_server_or_null(config: ServerConfig) -> Server:
    factory = server_factory_map.get(config.get_schema())

    if factory is None:
        return None
    else:
        return factory.create_server(config)


def create_client(urlOrConfig: str | ClientConfig) -> Client:
    Asserts.assert_null("urlOrConfig", urlOrConfig)
    config:ClientConfig

    if isinstance(urlOrConfig, str):
        config = ClientConfig(urlOrConfig)
    else:
        config = urlOrConfig

    client = create_client_or_null(config)

    if client is None:
        raise RuntimeError(f"No socketd client providers were found: {config.get_schema()}")
    else:
        return client

def create_client_or_null(config: ClientConfig) -> Client:
    Asserts.assert_null("config", config)

    factory = client_factory_map.get(config.get_schema())
    if factory is None:
        return None
    else:
        return factory.create_client(config)


def create_cluster_client(*urls):
    return ClusterClient(*urls)


# Initialize the client and server factory maps
load_factories([WsAioProvider(), TcpAioProvider()], server_factory_map)
load_factories([WsAioProvider(), TcpAioProvider()], client_factory_map)
