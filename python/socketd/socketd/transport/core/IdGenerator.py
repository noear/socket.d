from typing import Callable

from socketd.transport.utils.StrUtils import StrUtil

IdGenerator = Callable[[None], str]


def GuidGenerator():
    return StrUtil.guid()
