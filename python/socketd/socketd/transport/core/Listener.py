import abc


class Listener:
    @abc.abstractmethod
    async def on_open(self, session):
        pass

    @abc.abstractmethod
    async def on_message(self, session, message):
        pass

    @abc.abstractmethod
    def on_close(self, session):
        pass

    @abc.abstractmethod
    def on_error(self, session, error):
        pass
