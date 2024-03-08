from typing import Callable
from socketd.transport.client.ClientConfig import ClientConfig

ClientConfigHandler = Callable[[ClientConfig], None]