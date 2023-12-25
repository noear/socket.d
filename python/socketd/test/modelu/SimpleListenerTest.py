import uuid
from abc import ABC

from socketd.core.Listener import Listener
from socketd.core.config.ClientConfig import ClientConfig
from socketd.core.config.ServerConfig import ServerConfig
from socketd.core.module.Entity import Entity
from socketd.core.module.Message import Message
from socketd.core.module.StringEntity import StringEntity

from loguru import logger

from socketd.core.sync_api.AtomicRefer import AtomicRefer


class SimpleListenerTest(Listener, ABC):

    def __init__(self):
        self.server_counter = AtomicRefer(0)
        self.close_counter = AtomicRefer(0)
        self.message_counter = AtomicRefer(0)

    def on_open(self, session):
        pass

    async def on_message(self, session, message: Message):
        with self.server_counter:
            self.server_counter.set(self.server_counter.get() + 1)
        if message.is_request():
            with self.message_counter:
                self.message_counter.set(self.message_counter.get() + 1)
            await session.reply(message, StringEntity("reply"))
            await session.reply_end(message, StringEntity("ok test"))
            await session.reply(message, StringEntity("reply"))
        elif message.is_subscribe():
            await session.reply(message, StringEntity("reply"))
            await session.reply_end(message, StringEntity("ok test"))
            await session.reply(message, StringEntity("reply"))

    def on_close(self, session):
        logger.debug("客户端主动关闭了")
        with self.close_counter:
            self.close_counter.set(self.close_counter.get() + 1)

    def on_error(self, session, error):
        pass


def config_handler(config: ServerConfig | ClientConfig) -> ServerConfig | ClientConfig:
    config.set_is_thread(False)
    return config.id_generator(uuid.uuid4)


def send_and_subscribe_test(e: Entity):
    logger.info(e)
