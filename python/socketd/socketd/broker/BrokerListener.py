from socketd.broker.BroadcastBroker import BroadcastBroker
from socketd.broker.BrokerListenerBase import BrokerListenerBase
from socketd.exception.SocketDExecption import SocketDException
from socketd.transport.core import Entity
from socketd.transport.core.Flags import Flags
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session
from socketd.transport.core.entity.MessageBuilder import MessageBuilder
from socketd.utils.LogConfig import log
from socketd.utils.SessionUtils import SessionUtils


# 经纪人监听器（为不同的玩家转发消息）
class BrokerListener(BrokerListenerBase, BroadcastBroker):
    def __init__(self):
        super().__init__()

    async def on_open(self, session: Session):
        name = session.name()
        self.add_player(name, session)

    def on_close(self, session: Session):
        name = session.name()
        self.remove_player(name, session)

    async def on_message(self, requester: Session, message: Message):
        atName = message.at_name()

        if atName is None:
            if requester:
                await requester.send_alarm(message, "Broker message require '@' meta")
            else:
                raise SocketDException("Broker message require '@' meta")
            return

        if atName.__eq__("*"):
            # 广播模式（给所有玩家）
            nameAll = self.get_name_all()
            if nameAll is not None and len(nameAll) > 0:
                for name in nameAll:
                    self.forward_to_name(requester, message, name)
        elif atName.endswith("*"):
            # 群发模式（给同名的所有玩家）
            atName = atName[:-1]
            if not self.forward_to_name(requester, message, atName):
                if requester:
                    await requester.send_alarm(message, "Broker don't have '@" + atName + "' player")
                else:
                    raise SocketDException("Broker don't have '@" + atName + "' player")
        else:
            responder = self.get_player_any(atName, requester, message)
            if responder is not None:
                self.forward_to_session(requester, message, responder)
            else:
                if requester:
                    await requester.send_alarm(message, "Broker don't have '@" + atName + "' session")
                else:
                    raise SocketDException("Broker don't have '@" + atName + "' session")

    # 广播
    def broadcast(self, event:str, entity:Entity):
        self.onMessage(None, MessageBuilder()
                .flag(Flags.Message)
                .event(event)
                .entity(entity).build())

    # 批量转发消息
    def forward_to_name(self, requester: Session, message: Message, name: str) -> bool:
        playerAll = self.get_player_all(name)

        if playerAll is not None and len(playerAll) > 0:
            for responder in playerAll:
                if responder != requester:
                    if responder.is_valid():
                        self.forward_to_session(requester, message, responder)
                    else:
                        self.on_close(responder)
            return True
        else:
            return False

    # 转发消息
    def forward_to_session(self, requester: Session, message: Message, responder: Session):
        if message.is_request():
            def then_reply(reply: Entity):
                if SessionUtils.is_valid(requester):
                    requester.reply(message, reply)

            def then_error(err: Exception):
                if SessionUtils.is_valid(requester):
                    requester.send_alarm(message, err)

            responder.send_and_request(message.event(), message, -1).then_reply(then_reply).then_error(then_error)
            return

        if message.is_subscribe():
            def then_reply(reply: Entity):
                if SessionUtils.is_valid(requester):
                    if message.is_end():
                        requester.reply_end(message, reply)
                    else:
                        requester.reply(message, reply)

            def then_error(err: Exception):
                if SessionUtils.is_valid(requester):
                    requester.send_alarm(message, err)

            responder.send_and_subscribe(message.event(), message, -1).then_reply(then_reply).then_error(then_error)
            return

        responder.send(message.event(), message)

    def on_error(self, session: Session, error):
        log.warning("Broker error", error)
