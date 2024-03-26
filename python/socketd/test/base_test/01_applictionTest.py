import asyncio
import sys

from websockets.legacy.server import WebSocketServer

from socketd import SocketD
from socketd.transport.core import Entity
from socketd.transport.core.Session import Session
from socketd.transport.core.impl.LogConfig import log
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from test.modelu.SimpleListenerTest import SimpleListenerTest
from test.uitls import calc_async_time


# 超过一定数量会导致异步并发发生异常
COUNT = 10000

log.remove()
log.add(sys.stderr, level="INFO")


@calc_async_time
async def application_test():
    loop = asyncio.get_running_loop()
    server: Server = SocketD.create_server(ServerConfig("ws").port(9999))
    server_session: WebSocketServer = await server.listen(
        SimpleListenerTest()).start()
    await asyncio.sleep(1)
    client_session: Session = await SocketD.create_client("std:ws://127.0.0.1:9999").open()

    log.info(f"client send count: {COUNT} ...")
    # 单向发送
    @calc_async_time
    async def _send():
        tasks = []
        for _ in range(COUNT):
            tasks.append(loop.create_task(client_session.send("demo", StringEntity("test"))))
        await asyncio.wait(tasks)

    await _send()

    # 发送并请求（且，等待一个答复）
    @calc_async_time
    async def _send_and_request():
        tasks = []
        for _ in range(COUNT):
            tasks.append(loop.create_task(client_session.send_and_request("demo", StringEntity("你好"), 100)))
        data, _ = await asyncio.wait(tasks)
        tasks.clear()
        for req in data:
            tasks.append(req.result().await_result())
        await asyncio.gather(*tasks)
    await _send_and_request()

    # 发送并订阅（且，接收零个或多个答复流）
    @calc_async_time
    async def _send_and_subscribe():
        async def send_and_subscribe_test(_entity: Entity):
            log.debug(f"c::subscribe::{_entity.data_as_string()} {_entity}")

        tasks = []
        for _ in range(COUNT):
            tasks.append(loop.create_task(client_session.send_and_subscribe("demo", StringEntity("hi"), 100)))
        data, _ = await asyncio.wait(tasks)
        for req in data:
            tasks.append(req.result().then_reply(send_and_subscribe_test))

    await _send_and_subscribe()

    await asyncio.sleep(3)
    # 关闭客户端会话
    await client_session.close()
    # 关闭服务端端会话
    server_session.close()
    # 停止服务端端
    await server.stop()


if __name__ == "__main__":
    # logger.remove()
    # logger.add(sys.stderr, level="INFO")
    asyncio.run(application_test())
