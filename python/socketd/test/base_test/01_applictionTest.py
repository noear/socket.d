import asyncio

from websockets.legacy.server import WebSocketServer

from socketd import SocketD
from socketd.transport.core import Entity
from socketd.transport.core.Session import Session
from socketd.transport.server.ServerConfig import ServerConfig
from socketd.transport.core.entity.StringEntity import StringEntity
from socketd.transport.server.Server import Server
from socketd.transport.stream.RequestStream import RequestStream
from socketd.transport.stream.SubscribeStream import SubscribeStream
from test.modelu.SimpleListenerTest import SimpleListenerTest
from test.uitls import calc_async_time
from loguru import logger


@calc_async_time
async def application_test():
    server: Server = SocketD.create_server(ServerConfig("ws").port(9999))
    server_session: WebSocketServer = await server.listen(
        SimpleListenerTest()).start()
    await asyncio.sleep(1)
    client_session: Session = await SocketD.create_client("std:ws://127.0.0.1:9999").open()

    # 单向发送
    await client_session.send("demo", StringEntity("test.png"))
    # 发送并请求（且，等待一个答复）
    req: RequestStream = await client_session.send_and_request("demo", StringEntity("你好"), 100)
    entity: Entity = await req.await_result()
    print(entity.data_as_string())

    async def send_and_subscribe_test(_entity: Entity):
        logger.debug(f"c::subscribe::{_entity.data_as_string()} {_entity}")

    # 发送并订阅（且，接收零个或多个答复流）
    req: SubscribeStream = await client_session.send_and_subscribe("demo", StringEntity("hi"), 100)
    req.then_reply(send_and_subscribe_test)
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
