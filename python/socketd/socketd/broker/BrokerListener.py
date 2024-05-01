from socketd.broker.BrokerListenerBase import BrokerListenerBase
from socketd.transport.core import Entity
from socketd.transport.core.Message import Message
from socketd.transport.core.Session import Session


# 经纪人监听器（为不同的玩家转发消息）
class BrokerListener(BrokerListenerBase):
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
            await requester.send_alarm(message, "Broker message require '@' meta")
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
                await requester.send_alarm(message, "Broker don't have '@" + atName + "' player")
        else:
            responder = self.get_player_any(atName, requester, message)
            if responder is not None:
                self.forward_to_session(requester, message, responder)
            else:
                await requester.send_alarm(message, "Broker don't have '@" + atName + "' session")

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
                if requester.is_valid():
                    requester.reply(message, reply)

            def then_error(err: Exception):
                if requester.is_valid():
                    requester.send_alarm(message, err)

            responder.send_and_request(message.event(), message, -1).then_reply(then_reply).then_error(then_error)
            return

        if message.is_subscribe():
            def then_reply(reply: Entity):
                if requester.is_valid():
                    if message.is_end():
                        requester.reply_end(message, reply)
                    else:
                        requester.reply(message, reply)

            def then_error(err: Exception):
                if requester.is_valid():
                    requester.send_alarm(message, err)

            responder.send_and_subscribe(message.event(), message, -1).then_reply(then_reply).then_error(then_error)
            return

        responder.send(message.event(), message)

    def on_error(self, session: Session, error):
        ...
        # log.warning("Broker error", error)
