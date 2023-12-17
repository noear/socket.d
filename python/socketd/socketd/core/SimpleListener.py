from socketd.core.Listener import Listener


class SimpleListener(Listener):

    def on_open(self, session):
        pass

    async def on_message(self, session, message):
        pass

    def on_close(self, session):
        pass

    def on_error(self, session, error):
        pass