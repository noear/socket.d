from .Entity import Entity
from .Message import Message
from ..Costants import Constants, Flag


class MessageDefault(Message):
    def __init__(self):
        self.sid = Constants.DEF_SID
        self.topic = Constants.DEF_TOPIC
        self.entity: Entity = None
        self.flag = Flag.Unknown

    def get_flag(self):
        return self.flag

    def set_flag(self, flag):
        self.flag = flag
        return self

    def set_sid(self, sid):
        self.sid = sid
        return self

    def set_topic(self, topic):
        self.topic = topic
        return self

    def set_entity(self, entity):
        self.entity = entity
        return self

    def is_request(self):
        return self.flag == Flag.Request

    def is_subscribe(self):
        return self.flag == Flag.Subscribe

    def is_close(self):
        return self.flag == Flag.Close

    def get_sid(self):
        return self.sid

    def get_topic(self):
        return self.topic

    def get_entity(self):
        return self.entity

    def __str__(self):
        return f"Message{{sid='{self.sid}', topic='{self.topic}', entity={self.entity}}}"
