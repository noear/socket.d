from typing import Callable

from socketd.transport.client.ClientConnector import ClientConnector
from socketd.transport.core.ChannelInternal import ChannelInternal

ClientConnectHandler = Callable[[ClientConnector], ChannelInternal]

def ClientConnectHandlerDefault(connector:ClientConnector):
    return connector.connect()
