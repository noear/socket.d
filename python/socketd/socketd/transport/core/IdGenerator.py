from typing import Callable

from socketd.transport.utils.StrUtils import StrUtils

IdGenerator = Callable[[None], str]


def GuidGenerator():
    return StrUtils.guid()
