import asyncio
import logging
import functools
from typing import Callable, Any, Optional, Sequence

from websockets import LoggerLike, Origin, Subprotocol, WebSocketClientProtocol, HeadersLike
from websockets.extensions import ClientExtensionFactory
from websockets.extensions.permessage_deflate import enable_client_permessage_deflate
from websockets.headers import validate_subprotocols
from websockets.http import USER_AGENT
from websockets.legacy.client import connect
from websockets.uri import parse_uri


class AIOConnect(connect):
    MAX_REDIRECTS_ALLOWED = 10

    def __init__(self, uri: str, client: 'WsAioClient', *,
                 create_protocol: Optional[Callable[..., WebSocketClientProtocol]] = None,
                 logger: Optional[LoggerLike] = None, compression: Optional[str] = "deflate",
                 origin: Optional[Origin] = None, extensions: Optional[Sequence[ClientExtensionFactory]] = None,
                 subprotocols: Optional[Sequence[Subprotocol]] = None, extra_headers: Optional[HeadersLike] = None,
                 user_agent_header: Optional[str] = USER_AGENT, open_timeout: Optional[float] = 10,
                 ping_interval: Optional[float] = 20, ping_timeout: Optional[float] = 20,
                 close_timeout: Optional[float] = None, max_size: Optional[int] = 2 ** 20,
                 max_queue: Optional[int] = 2 ** 5, read_limit: int = 2 ** 16, write_limit: int = 2 ** 16,
                 message_loop = None,
                 **kwargs: Any) -> None:

        timeout: Optional[float] = 10
        # If both are specified, timeout is ignored.
        if close_timeout is None:
            close_timeout = timeout

        # Backwards compatibility: recv() used to return None on closed connections
        legacy_recv: bool = kwargs.pop("legacy_recv", False)

        # Backwards compatibility: the loop parameter used to be supported.
        _loop: Optional[asyncio.AbstractEventLoop] = kwargs.get("loop")
        if _loop is None:
            loop = asyncio.get_event_loop()
        else:
            loop = _loop

        wsuri = parse_uri(uri)
        if wsuri.secure:
            kwargs.setdefault("ssl", True)
        elif kwargs.get("ssl") is not None:
            raise ValueError(
                "connect() received a ssl argument for a ws:// URI, "
                "use a wss:// URI to enable TLS"
            )

        if compression == "deflate":
            extensions = enable_client_permessage_deflate(extensions)
        elif compression is not None:
            raise ValueError(f"unsupported compression: {compression}")

        if subprotocols is not None:
            validate_subprotocols(subprotocols)

        factory = functools.partial(
            create_protocol,
            client=client,
            logger=logger,
            origin=origin,
            extensions=extensions,
            subprotocols=subprotocols,
            extra_headers=extra_headers,
            user_agent_header=user_agent_header,
            ping_interval=ping_interval,
            ping_timeout=ping_timeout,
            close_timeout=close_timeout,
            max_size=max_size,
            max_queue=max_queue,
            read_limit=read_limit,
            write_limit=write_limit,
            host=wsuri.host,
            port=wsuri.port,
            secure=wsuri.secure,
            legacy_recv=legacy_recv,
            loop=loop,
            message_loop=message_loop
        )

        if kwargs.pop("unix", False):
            path: Optional[str] = kwargs.pop("path", None)
            create_connection = functools.partial(
                loop.create_unix_connection, factory, path, **kwargs
            )
        else:
            host: Optional[str]
            port: Optional[int]
            if kwargs.get("sock") is None:
                host, port = wsuri.host, wsuri.port
            else:
                # If sock is given, host and port shouldn't be specified.
                host, port = None, None
                if kwargs.get("ssl"):
                    kwargs.setdefault("server_hostname", wsuri.host)
            # If host and port are given, override values from the URI.
            host = kwargs.pop("host", host)
            port = kwargs.pop("port", port)
            create_connection = functools.partial(
                loop.create_connection, factory, host, port, **kwargs
            )

        self.open_timeout = open_timeout
        if logger is None:
            logger = logging.getLogger("websockets.client")
        self.logger = logger

        # This is a coroutine function.
        self._create_connection = create_connection
        self._uri = uri
        self._wsuri = wsuri
        self.channel = None

    def get_channel(self):
        return self.channel

