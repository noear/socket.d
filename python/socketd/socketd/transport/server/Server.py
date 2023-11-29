from typing import Callable

from websockets.legacy.server import Serve

from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.Processor import Processor
from socketd.core.Listener import Listener


class Server:
    def config(self, consumer: Callable[['ServerConfig'], None]) -> 'Server': ...

    def process(self, processor: 'Processor') -> 'Server': ...

    def listen(self, listener: 'Listener') -> 'Server': ...

    def start(self) -> Serve: ...

    def stop(self) -> None: ...
