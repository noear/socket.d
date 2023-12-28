import typing

from socketd.transport.core.Listener import Listener

PathMapper: typing.Type[dict[Listener]] = dict[Listener]


class PathMapperDefault(PathMapper):
    pass


