import asyncio
from abc import ABC, abstractmethod

from socketd.transport.core.stream.Stream import Stream


class StreamManger(ABC):

    @abstractmethod
    def add_stream(self, sid: str, stream: 'StreamInternal'): ...

    @abstractmethod
    def get_stream(self, sid: str) -> Stream: ...

    @abstractmethod
    def remove_stream(self, sid: str) -> None: ...


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
