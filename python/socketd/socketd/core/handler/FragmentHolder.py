from socketd.core.module.Message import Message


class FragmentHolder:

    def __init__(self, index: int, message: Message):
        self.index = index
        self.message = message
