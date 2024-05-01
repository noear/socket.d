import asyncio

from socketd import SocketD
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.utils.LogConfig import log
from socketd.transport.core.listener.EventListener import EventListener


async def main():
    server = await (SocketD.create_server("sd:ws")
                    .listen(EventListener()
                            .do_on_open(lambda s:
                                        # 会话打开时
                                        log.info(s.session_id())
                                        ).do_on_message(lambda s, m:
                                                        # 收到任意消息时（方便做统一的日志打印）
                                                        log.info(m.data_as_string())
                                                        ).do_on("/demo", lambda s, m:
    # 收到"/demo"事件的消息时。如果是请求或订阅？则进行签复
    (m.is_request() or m.is_subscribe()) and s.reply_end(m, StringEntity("And you too."))
                                                                )).start())

    await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())