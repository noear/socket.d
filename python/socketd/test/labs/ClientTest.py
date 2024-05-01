import asyncio

from socketd import SocketD
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.utils.LogConfig import log


async def main():
    # 打开客户端会话，并监听（用 url 形式打开）
    session = await (SocketD.create_client("sd:ws://127.0.0.1:8602/?token=1b0VsGusEkddgr3d")
                     .open())

    entity = StringEntity("Hello wrold!").meta_put("sender", "noear")

    # 发送
    session.send("/demo", entity)

    # 发送并请求（且，等待一个答复。否则超时异常）
    rep = await session.send_and_request("/demo", entity).waiter()
    log.info(rep.data_as_string())

    session.send_and_request("/demo", entity).then_reply(lambda reply:
        # 打印
        log.info(reply.data_as_string())
    ).then_error(lambda err:
        log.error(err)
    )

    # 发送并订阅（且，接收零个或多个答复流）
    session.send_and_subscribe("/demo", entity).then_reply(lambda reply:
        # 打印
        log.info(reply.data_as_string()) or (reply.is_end() and log.info("the end!"))
    ).then_error(lambda err:
        log.error(err)
    )

    await asyncio.Future()


if __name__ == "__main__":
    asyncio.run(main())