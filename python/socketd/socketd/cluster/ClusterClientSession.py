import uuid

from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.core import Entity
from socketd.transport.stream.RequestStream import RequestStream
from socketd.transport.stream.SendStream import SendStream
from socketd.transport.stream.SubscribeStream import SubscribeStream
from socketd.transport.utils.sync_api.AtomicRefer import AtomicRefer


class ClusterClientSession(ClientSession):

    def __init__(self, _sessionSet):
        self._sessionSet: list[ClientSession] = _sessionSet
        self._sessionId = str(uuid.uuid4()).replace("-", "")
        self._sessionRoundCounter = AtomicRefer(0)

    def is_valid(self) -> bool:
        for session in self._sessionSet:
            if session.is_valid():
                return True
        return False

    def __get_session(self):
        if len(self._sessionSet) == 0:
            raise SocketDException("No session!")
        _sessions = [i for i in self._sessionSet if i.is_valid()]
        if len(_sessions) == 0:
            raise SocketDException("No session is available!")
        with self._sessionRoundCounter as i:
            return self._sessionSet[i % len(self._sessionSet)]

    def get_session_id(self) -> str:
        return self._sessionId

    async def send(self, event: str, content: Entity) -> SendStream:
        return await self.__get_session().send(event, content)

    async def send_and_request(self, event: str, content: Entity, timeout: int) -> RequestStream:
        return await self.__get_session().send_and_request(event, content, timeout)

    async def send_and_subscribe(self, event: str, content: Entity, timeout: int = 0) -> SubscribeStream:
        return await self.__get_session().send_and_subscribe(event, content, timeout)

    async def close(self):
        for session in self._sessionSet:
            try:
                await session.close()
            except RuntimeError as e:
                pass

    def reconnect(self) -> None:
        for session in self._sessionSet:
            if not session.is_valid():
                session.reconnect()
