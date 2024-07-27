from typing import Dict

from socketd.cluster.ClusterClient import ClusterClient
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
    return "2.5.11"

def protocol_name() -> str:
    return "Socket.D"

def protocol_version() -> str:
    return "1.0"


__client_provider_map: Dict[str, ClientProvider] = {}
__server_provider_map: Dict[str, ServerProvider] = {}

def register_client_provider(clientProvider: ClientProvider):
    for s in clientProvider.schema():
        __client_provider_map[s] = clientProvider

def register_server_provider(serverProvider: ServerProvider):
    for s in serverProvider.schema():
        __server_provider_map[s] = serverProvider

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
    provider = __server_provider_map.get(config.get_schema())

    if provider is None:
        return None
    else:
        return provider.create_server(config)


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

def create_client_or_null(config: ClientConfig) -> Client | None:
    Asserts.assert_null("config", config)

    provider = __client_provider_map.get(config.get_schema())
    if provider is None:
        return None
    else:
        return provider.create_client(config)


def create_cluster_client(*urls) -> Client:
    return ClusterClient(urls)


# Initialize the client and server provider maps
for p in [WsAioProvider(), TcpAioProvider()]:
    register_client_provider(p)
    register_server_provider(p)
