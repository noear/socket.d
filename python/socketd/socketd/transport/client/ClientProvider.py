from .Client import Client
from socketd.transport.client.ClientConfig import ClientConfig


class ClientProvider:

    """
        协议架构
    """
    def schema(self) -> list[str]: ...

    """
        创建客户端
    """
    def create_client(self, clientConfig: ClientConfig) -> Client: ...
