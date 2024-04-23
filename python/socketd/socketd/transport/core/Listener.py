import abc

from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session


class Listener(abc.ABC):
    @abc.abstractmethod
    async def on_open(self, session: Session):
        pass

    @abc.abstractmethod
    async def on_message(self, session: Session, message: Message):
        pass

    @abc.abstractmethod
    async def on_close(self, session: Session):
        pass

    @abc.abstractmethod
    async def on_error(self, session: Session, error):
        pass
