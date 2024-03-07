import asyncio
from typing import Coroutine, Optional, Tuple

from socketd.transport.server.ServerBase import ServerBase
from socketd.transport.server.ServerConfig import ServerConfig


class TCPAIOServer(ServerBase):

    def __init__(self, config: ServerConfig, assistant):
        super().__init__(config, assistant)
        self._server: Optional[asyncio.Server] = None

    # 服务器的回调函数
    async def handler(self, reader, writer):  # reader和writer参数是asyncio.start_server生成异步服务器后自动传入进来的
        while True:  # 循环接受数据，直到套接字关闭
            # wait_for等待读取数据，第二个参数为等待时间(None表示无限等待)
            data = await asyncio.wait_for(reader.read(2 ** 10), self._config.get_idle_timeout())
            if not data:
                writer.close()  # 关闭套接字
                await writer.wait_closed()  # 等待套接字完全关闭
                return
            writer.write(data.encode())  # 发送数据
            await writer.drain()  # 发送数据后，清空套接字

    async def start(self):
        # 生成一个服务器
        self._server: asyncio.Server = await asyncio.start_server(self.handler,
                                                                  host=self._config.get_host(),
                                                                  port=self._config.get_port())
        # 获取请求连接的客户端信息
        addr = self._server.sockets[0].getsockname()
        await self._server.start_serving()
        # await self._server.serve_forever()
        return self._server

    async def stop(self):
        # 等等执行完成
        await self._server.wait_closed()
        self._server.close()
