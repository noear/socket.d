from typing import Callable

from socketd.transport.core.Session import Session


ClientHeartbeatHandler = Callable[[Session], None]


async def ClientHeartbeatHandlerDefault(session: Session):
    await session.send_ping()
