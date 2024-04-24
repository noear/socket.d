from abc import ABC, abstractmethod

class StreamManger(ABC):

    @abstractmethod
    def add_stream(self, sid: str, stream: 'StreamInternal'): ...

    @abstractmethod
    def get_stream(self, sid: str) -> 'StreamInternal': ...

    @abstractmethod
    def remove_stream(self, sid: str): ...

