import uuid
from abc import ABC

from socketd.core.Listener import Listener
from socketd.core.module.Message import Message
from socketd.core.module.StringEntity import StringEntity


class SimpleListenerTest(Listener, ABC):

    def on_open(self, session):
        # print("open")
        pass

    def on_message(self, session, message: Message):
        if message.is_request():
            session.replyEnd(message, StringEntity("test"))
        elif message.is_subscribe():
            session.replyEnd(message, StringEntity("test"))
        # print("on_message")

    def on_close(self, session):
        pass

    def on_error(self, session, error):
        pass

def idGenerator(config):
    return config.id_generator(uuid.uuid4)
