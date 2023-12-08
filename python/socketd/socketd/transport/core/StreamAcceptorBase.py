from socketd.transport.SteamAcceptor import StreamAcceptor
from asyncio.futures import Future


class StreamAcceptorBase(StreamAcceptor):
    """流接收器基类"""

    def insuranceFuture(self) -> Future:
        pass
