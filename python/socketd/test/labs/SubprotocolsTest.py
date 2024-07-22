import asyncio

from socketd import SocketD
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.core.listener.SimpleListener import SimpleListener
from socketd.utils.LogConfig import log


class ListenerImpl(SimpleListener):
    async def on_open(self, s: Session):
        # 会话打开时
        log.info(s.session_id())
        pass
    async def on_message(self, s: Session, m: Message):
        # 收到任意消息时（方便做统一的日志打印）
        log.info(m.data_as_string())


async def main():
    await (SocketD.create_server("sd:ws")
                    .config(lambda c: c.port(8602).use_subprotocols(True))
                    .listen(ListenerImpl()).start())

    session = await (SocketD.create_client("sd:ws://localhost:8602")
                     .config(lambda c: c.use_subprotocols(False))
                     .open_or_throw())

    session.send("/demo", StringEntity("hello"))

    await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())