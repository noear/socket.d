import asyncio
from abc import ABC, abstractmethod

from socketd.transport.stream.StreamManger import StreamManger


class Stream(ABC):

    @abstractmethod
    def sid(self) -> str:
        """流ID"""
        ...

    @abstractmethod
    def is_done(self):
        """是否结束接收"""
        ...



class StreamInternal(Stream, ABC):

    @abstractmethod
    def demands(self) -> int: ...

    @abstractmethod
    def timeout(self) -> float: ...

    @abstractmethod
    def insurance_start(self, streamManger: StreamManger, streamTimeout: float) -> None: ...

    @abstractmethod
    def insurance_cancel(self) -> None: ...

    @abstractmethod
    async def on_reply(self, reply) -> None | asyncio.Future: ...

    @abstractmethod
    def on_error(self, error): ...

    @abstractmethod
    def on_progress(self, is_send, val, max_val): ...