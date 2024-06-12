import abc

from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session


class Listener(abc.ABC):
    # 打开时
    @abc.abstractmethod
    async def on_open(self, session: Session):
        pass

    # 收到消息时
    @abc.abstractmethod
    async def on_message(self, session: Session, message: Message):
        pass

    # 收到答复时
    async def on_reply(self, session: Session, message: Message):
        pass

    # 发送消息时
    async def on_send(self, session: Session, message: Message):
        pass

    # 关闭时
    @abc.abstractmethod
    async def on_close(self, session: Session):
        pass

    # 出错时
    @abc.abstractmethod
    async def on_error(self, session: Session, error):
        pass
