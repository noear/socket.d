import asyncio
import time
from loguru import logger

from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.module.StringEntity import StringEntity
from test.SimpleListenerTest import idGenerator

from uitls import calc_async_time


@calc_async_time
async def main():

    client_session: Session = SocketD.create_client("ws://127.0.0.1:7779") \
        .config(idGenerator).open()
    start_time = time.monotonic()
    for _ in range(100000):
        await client_session.send("demo", StringEntity("test"))
    # await asyncio.gather(*[client_session.send("demo", StringEntity("test")) for _ in range(100000)])
    end_time = time.monotonic()
    logger.debug(f"Coroutine send took {(end_time - start_time) * 1000} monotonic to complete.")
    await client_session.close()

    # await server_session.stop


if __name__ == "__main__":
    asyncio.get_event_loop().run_until_complete(main())
    # asyncio.run(main())
