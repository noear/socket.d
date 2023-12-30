#客户端

from typing import Callable

from socketd.transport.client.ClientConfig import ClientConfig


class Client:
    def heartbeatHandler(self, handler: Callable) -> 'Client':...

    def config(self, consumer: Callable[['ClientConfig'], None]) -> 'Client':...

    def process(self, processor: Callable) -> 'Client':...

    def listen(self, listener: Callable) -> 'Client':...

    def open(self):...

