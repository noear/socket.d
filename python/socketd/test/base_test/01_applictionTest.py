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
from socketd.transport.stream.RequestStream import RequestStream
from socketd.transport.stream.SubscribeStream import SubscribeStream
from test.modelu.SimpleListenerTest import SimpleListenerTest
from test.uitls import calc_async_time

COUNT = 1000

log.remove()
log.add(sys.stderr, level="INFO")


@calc_async_time
async def application_test():
    server: Server = SocketD.create_server(ServerConfig("ws").port(9999))
    server_session: WebSocketServer = await server.listen(
        SimpleListenerTest()).start()
    await asyncio.sleep(1)
    client_session: Session = await SocketD.create_client("std:ws://127.0.0.1:9999").open()

    # 单向发送
    @calc_async_time
    async def _send():
        for _ in range(COUNT):
            await client_session.send("demo", StringEntity("test"))

    await _send()

    # 发送并请求（且，等待一个答复）
    @calc_async_time
    async def _send_and_request():
        for _ in range(COUNT):
            req: RequestStream = await client_session.send_and_request("demo", StringEntity("你好"), 100)
            entity: Entity = await req.await_result()

    # await _send_and_request()

    # 发送并订阅（且，接收零个或多个答复流）
    @calc_async_time
    async def _send_and_subscribe():
        async def send_and_subscribe_test(_entity: Entity):
            log.debug(f"c::subscribe::{_entity.data_as_string()} {_entity}")

        for _ in range(COUNT):
            req: SubscribeStream = await client_session.send_and_subscribe("demo", StringEntity("hi"), 100)
            req.then_reply(send_and_subscribe_test)

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
