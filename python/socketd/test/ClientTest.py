import asyncio
import uuid
from abc import ABC


from socketd.core.Listener import Listener
from socketd.core.Session import Session
from socketd.core.SocketD import SocketD
from socketd.core.module.Message import Message
from socketd.core.module.StringEntity import StringEntity


class SimpleListener(Listener, ABC):

    def on_open(self, session):
        print("open")

    def on_message(self, session, message: Message):
        if message.is_request():
            session.replyEnd(message, StringEntity("test"))
        elif message.is_subscribe():
            session.replyEnd(message, StringEntity("test"))
        print("on_message")

    def on_close(self, session):
        pass

    def on_error(self, session, error):
        pass


def idGenerator(config):
    return config.id_generator(uuid.uuid4)


async def main():
    # server = SocketD.create_server(ServerConfig("ws").setPort(7779))
    # server_session: WsAioServer = server.config(idGenerator).listen(
    #     SimpleListener()).start()
    #
    # await asyncio.sleep(3)

    client_session: Session = SocketD.create_client("ws://127.0.0.1:7779") \
        .config(idGenerator).open()

    await client_session.send("demo", StringEntity("test"))
    # await client_session.send("demo2", StringEntity("test"))
    await asyncio.sleep(1)
    # asyncio.get_event_loop().run_forever()
    await client_session.close()
    # await server_session.stop




if __name__ == "__main__":
    asyncio.run(main())

