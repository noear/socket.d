from socketd.transport.core.Listener import Listener
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session


class SimpleListener(Listener):

    async def on_open(self, session: Session):
        pass

    async def on_message(self, session: Session, message: Message):
        pass

    async def on_close(self, session: Session):
        pass

    def on_error(self, session: Session, error):
        pass
