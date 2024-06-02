from socketd.cluster.LoadBalancer import LoadBalancer
from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.client.ClientSession import ClientSession
from socketd.transport.core import Entity
from socketd.transport.stream.RequestStream import RequestStream
from socketd.transport.stream.SendStream import SendStream
from socketd.transport.stream.SubscribeStream import SubscribeStream
from socketd.utils.StrUtils import StrUtils


class ClusterClientSession(ClientSession):

    def __init__(self, _sessionSet):
        self.__sessionSet: list[ClientSession] = _sessionSet
        self.__sessionId = StrUtils.guid()

    def get_session_all(self) -> list[ClientSession]:
        return self.__sessionSet

    def get_session_any(self, diversionOrNull: str | None) -> ClientSession:
        session: ClientSession

        if diversionOrNull:
            session = LoadBalancer.get_any_by_poll(self.__sessionSet)
        else:
            session = LoadBalancer.get_any_by_hash(self.__sessionSet, diversionOrNull)

        if session:
            return session
        else:
            raise SocketDException("No session is available!")

    def is_valid(self) -> bool:
        for session in self.__sessionSet:
            if session.is_valid():
                return True
        return False

    def is_active(self) -> bool:
        for session in self.__sessionSet:
            if session.is_active():
                return True
        return False

    def is_closing(self) -> bool:
        for session in self.__sessionSet:
            if session.is_closing():
                return True
        return False

    def session_id(self) -> str:
        return self.__sessionId

    def send(self, event: str, content: Entity) -> SendStream:
        sender = self.get_session_any(None)

        return sender.send(event, content)

    def send_and_request(self, event: str, content: Entity, timeout: float = 0) -> RequestStream:
        sender = self.get_session_any(None)

        return sender.send_and_request(event, content, timeout)

    def send_and_subscribe(self, event: str, content: Entity, timeout: float = 0) -> SubscribeStream:
        sender = self.get_session_any(None)

        return sender.send_and_subscribe(event, content, timeout)

    async def preclose(self):
        for session in self.__sessionSet:
            try:
                await session.preclose()
            except RuntimeError as e:
                pass

    async def close(self):
        for session in self.__sessionSet:
            try:
                await session.close()
            except RuntimeError as e:
                pass

    async def reconnect(self):
        for session in self.__sessionSet:
            if not session.is_valid():
                await session.reconnect()
