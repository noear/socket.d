import unittest
import asyncio
import time

from loguru import logger
from websockets import serve, WebSocketServerProtocol, connect

from socketd.utils.AsyncUtils import AsyncUtils
from test.uitls import calc_async_time


class Test_websockets(unittest.TestCase):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._loop = asyncio.new_event_loop()
        self.top = AsyncUtils.run_forever(self._loop)
        # self._loop2 = asyncio.new_event_loop()
        # self.top2 = AsyncUtil.run_forever(self._loop2)

    async def on_message(self, websocket: WebSocketServerProtocol, path: str):
        """ws_handler"""
        while True:
            message = await websocket.recv()
            logger.debug(message)
            if message == "close":
                await websocket.close()
                break
            if message is None:
                break

    async def _server(self):
        # set this future to exit the server
        __server = await serve(ws_handler=self.on_message, host="0.0.0.0", port=7780,
                               loop=self._loop,
                               ping_interval=109,
                               ping_timeout=5)

    async def client(self):
        uri = "ws://localhost:7780"
        async with connect(uri, ping_interval=100,
                               ping_timeout=590) as websocket:
            start_time = time.monotonic()
            for _ in range(10):
                await websocket.send("test")
            end_time = time.monotonic()
            logger.info(f"Coroutine send took {(end_time - start_time) * 1000.0} monotonic to complete.")
            await websocket.send("close")
            await websocket.close()

    def test_application(self):
        # logger.remove()
        # logger.add(sys.stderr, level="INFO")

        @calc_async_time
        async def _main():
            stop = asyncio.Future()
            await asyncio.gather(self._server(), self.client())
            stop.set_result(1)
            await stop
            # self.top.set_result(1)
            # self._loop.stop()
            # self.top2.set_result(1)
            # self._loop2.stop()

        asyncio.run(_main())


if __name__ == "__main__":
    Test_websockets().test_application()
