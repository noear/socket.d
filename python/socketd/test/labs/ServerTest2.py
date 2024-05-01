import asyncio

from socketd import SocketD
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.utils.LogConfig import log
from socketd.transport.core.Listener import Listener


class ListenerImpl(Listener):
    async def on_open(self, s: Session):
        # 会话打开时
        log.info(s.session_id())
        pass

    async def on_message(self, s: Session, m: Message):
        # 收到任意消息时（方便做统一的日志打印）
        log.info(m.data_as_string())

        if m.event() == "/demo":
            # 收到"/demo"事件的消息时。如果是请求或订阅？则进行签复
            if m.is_request() or m.is_subscribe():
                await s.reply_end(m, StringEntity("And you too."))

    async def on_close(self, s: Session):
        log.info(s.session_id())
        pass

    async def on_error(self, s: Session, error):
        log.info(s.session_id())
        log.info(error)
        pass


async def main():
    server = await (SocketD.create_server("sd:ws")
                    .listen(ListenerImpl()).start())

    await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())