import socket
from asyncio import StreamReader, StreamWriter


class TCPStreamIO:

    __slots__ = ["_sock", "_reader", "_writer"]

    def __init__(self, server: socket.socket, reader: StreamReader, writer: StreamWriter):
        self._sock: socket.socket = server
        self._reader = reader
        self._writer = writer

    @property
    def sock(self):
        return self._sock

    @property
    def reader(self):
        return self._reader

    @property
    def writer(self):
        return self._writer
