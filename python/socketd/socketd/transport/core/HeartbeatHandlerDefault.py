from abc import ABC

from socketd.core.Session import Session


class HeartbeatHandler(ABC):

    def heartbeat(self, session: Session): ...


class HeartbeatHandlerDefault(HeartbeatHandler):

    async def heartbeat(self, session: Session):
        await session.send_ping()
