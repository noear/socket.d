import asyncio
import sys
import time
from loguru import logger

from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.module.StringEntity import StringEntity
from test.modelu.SimpleListenerTest import idGenerator

from uitls import calc_async_time

logger.remove()
logger.add(sys.stderr, level="INFO")
# logger.add(sys.stderr, level="DEBUG")


@calc_async_time
async def main():
    client_session: Session = await SocketD.create_client("ws://127.0.0.1:7779").config(idGenerator).open()
    start_time = time.monotonic()
    for _ in range(100000):
        await client_session.send("demo", StringEntity("test"))
        # e = await client_session.send_and_request("demo", StringEntity("test"), 100)
        # logger.debug("send_and_request={e}", e=e)
    # await asyncio.gather(*[client_session.send("demo", StringEntity("test")) for _ in range(10000)])
    end_time = time.monotonic()
    logger.info(f"Coroutine send took {(end_time - start_time) * 1000} monotonic to complete.")
    await client_session.close()

    # await server_session.stop


if __name__ == "__main__":
    asyncio.get_event_loop().run_until_complete(main())
    # asyncio.run(main())
