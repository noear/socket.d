from typing import Callable

from socketd.transport.core.Listener import Listener
from socketd.transport.core.Processor import Processor
from socketd.transport.server.ServerConfig import ServerConfig


class Server:
    def config(self, consumer: Callable[[ServerConfig], None]) -> Server:...

    def process(self, processor: Processor) -> Server:...

    def listen(self, listener: Listener) -> Server:...

    def start(self) -> None:...

    def stop(self) -> None:...
