import asyncio
import sys
import time
from loguru import logger

from socketd.transport.core.Session import Session
from socketd.SocketD import SocketD
from socketd.transport.core.Entity import Entity
from socketd.transport.core.entity.StringEntity import StringEntity
from test.uitls import calc_async_time

from test.modelu.SimpleListenerTest import config_handler, SimpleListenerTest

# logger.remove()
# logger.add(sys.stderr, level="INFO")


# logger.add(sys.stderr, level="DEBUG")
def send_and_subscribe_test(e: Entity):
    print(e)


@calc_async_time
async def main():
    client_session: Session = await SocketD.create_client("std:ws://127.0.0.1:9100").config(config_handler).open()
    start_time = time.monotonic()
    for _ in range(10):
        await client_session.send("demo", StringEntity("test.png"))
        # e = await client_session.send_and_request("demo", StringEntity("test.png"), 100)
        # await client_session.send_and_subscribe("demo", StringEntity("test.png"), send_and_subscribe_test, 100)
        # logger.debug("send_and_request={e}", e=e)
    # await asyncio.gather(*[client_session.send("demo", StringEntity("test.png")) for _ in range(10000)])
    end_time = time.monotonic()
    logger.info(f"Coroutine send took {(end_time - start_time) * 1000} monotonic to complete.")
    await client_session.close()

    # await server_session.stop


if __name__ == "__main__":
    asyncio.run(main())
