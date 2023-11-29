import asyncio
import uuid
import time
from abc import ABC

from websockets.legacy.client import Connect
from websockets.legacy.server import Serve

from socketd.core.Listener import Listener
from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.module.Message import Message
from socketd.core.module.StringEntity import StringEntity
from socketd_websocket.WsAioServer import WsAioServer
from socketd_websocket.impl.AIOServe import AIOServe


class SimpleListener(Listener, ABC):

    def on_open(self, session):
        pass

    def on_message(self, session: Session, message: Message):
        if message.is_request():
            session.reply_end(message, StringEntity("test"))
        elif message.is_subscribe():
            session.reply_end(message, StringEntity("test"))


    def on_close(self, session):
        pass

    def on_error(self, session, error):
        pass


def idGenerator(config):
    return config.id_generator(uuid.uuid4)


def main():
    server = SocketD.create_server(ServerConfig("ws").setPort(7779))
    server_session: Serve = server.config(idGenerator).listen(
        SimpleListener()).start()
    asyncio.get_event_loop().run_forever()


if __name__ == "__main__":
    main()
