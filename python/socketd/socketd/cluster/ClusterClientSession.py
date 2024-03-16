from socketd.cluster.LoadBalancer import LoadBalancer
from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.core import Entity
from socketd.transport.stream.RequestStream import RequestStream
from socketd.transport.stream.SendStream import SendStream
from socketd.transport.stream.SubscribeStream import SubscribeStream
from socketd.transport.utils.StrUtil import StrUtil


class ClusterClientSession(ClientSession):

    def __init__(self, _sessionSet):
        self._sessionSet: list[ClientSession] = _sessionSet
        self._sessionId = StrUtil.guid()

    def get_session_all(self) -> list[ClientSession]:
        return self._sessionSet

    def get_session_any(self, diversionOrNull:str) ->  ClientSession:
        session:ClientSession

        if diversionOrNull:
            session = LoadBalancer.getAnyByPoll(self._sessionSet)
        else:
            session = LoadBalancer.getAnyByHash(self._sessionSet, diversionOrNull)

        if session:
            return session
        else:
            raise SocketDException("No session is available!")

    def is_valid(self) -> bool:
        for session in self._sessionSet:
            if session.is_valid():
                return True
        return False

    def is_closing(self) ->bool:
        for session in self._sessionSet:
            if session.is_closing():
                return True
        return False

    def get_session_id(self) -> str:
        return self._sessionId

    async def send(self, event: str, content: Entity) -> SendStream:
        return await self.get_session_any(None).send(event, content)

    async def send_and_request(self, event: str, content: Entity, timeout: int) -> RequestStream:
        return await self.get_session_any(None).send_and_request(event, content, timeout)

    async def send_and_subscribe(self, event: str, content: Entity, timeout: int = 0) -> SubscribeStream:
        return await self.get_session_any(None).send_and_subscribe(event, content, timeout)

    async def close_starting(self):
        for session in self._sessionSet:
            try:
                await session.close_starting()
            except RuntimeError as e:
                pass

    async def close(self):
        for session in self._sessionSet:
            try:
                await session.close()
            except RuntimeError as e:
                pass

    def reconnect(self):
        for session in self._sessionSet:
            if not session.is_valid():
                session.reconnect()
