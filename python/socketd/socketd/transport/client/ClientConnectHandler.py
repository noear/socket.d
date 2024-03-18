from typing import Callable, Coroutine, Any

from socketd.transport.client.ClientConnector import ClientConnector
from socketd.transport.core.ChannelInternal import ChannelInternal

ClientConnectHandler = Callable[[ClientConnector], ChannelInternal]


def ClientConnectHandlerDefault(connector: ClientConnector) -> Coroutine[Any, Any, ChannelInternal]:
    return connector.connect()
