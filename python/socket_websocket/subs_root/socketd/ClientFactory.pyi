from socketd.core.config.ClientConfig import ClientConfig


class ClientFactory:

    """
        协议架构
    """
    def schema(self) -> str: ...

    """
        创建客户端
    """
    def create_client(self, clientConfig: ClientConfig) -> 'IClient': ...
