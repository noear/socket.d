import asyncio
import functools
from concurrent.futures import Executor
from types import TracebackType
from typing import Callable, Union, Awaitable, Any, Optional, Sequence, Type, Generator
from wsgiref.headers import Headers

from websockets import WebSocketServerProtocol, LoggerLike, Origin, Subprotocol
from websockets.extensions import ServerExtensionFactory
from websockets.extensions.permessage_deflate import enable_server_permessage_deflate
from websockets.headers import validate_subprotocols
from websockets.http import USER_AGENT
from websockets.legacy.server import HeadersLikeOrCallable, HTTPResponse, remove_path_argument, WebSocketServer, Serve


class AIOServe(Serve):

    def __init__(self, ws_handler: Union[
        Callable[[WebSocketServerProtocol], Awaitable[Any]],
        Callable[[WebSocketServerProtocol, str], Awaitable[Any]],  # deprecated
    ], host: Optional[Union[str, Sequence[str]]] = None, port: Optional[int] = None, ws_aio_server=None, *,
                 create_protocol: Optional[Callable[..., WebSocketServerProtocol]] = None,
                 logger: Optional[LoggerLike] = None, compression: Optional[str] = "deflate",
                 origins: Optional[Sequence[Optional[Origin]]] = None,
                 extensions: Optional[Sequence[ServerExtensionFactory]] = None,
                 subprotocols: Optional[Sequence[Subprotocol]] = None,
                 extra_headers: Optional[HeadersLikeOrCallable] = None, server_header: Optional[str] = USER_AGENT,
                 process_request: Optional[
                     Callable[[str, Headers], Awaitable[Optional[HTTPResponse]]]
                 ] = None, select_subprotocol: Optional[
                Callable[[Sequence[Subprotocol], Sequence[Subprotocol]], Subprotocol]
            ] = None, open_timeout: Optional[float] = 10, ping_interval: Optional[float] = 20,
                 ping_timeout: Optional[float] = 20, close_timeout: Optional[float] = None,
                 max_size: Optional[int] = 2 ** 20, max_queue: Optional[int] = 2 ** 5, read_limit: int = 2 ** 16,
                 write_limit: int = 2 ** 16, executor: Executor = None, **kwargs: Any) -> None:
        _loop: Optional[asyncio.AbstractEventLoop] = asyncio.get_event_loop()
        super().__init__(ws_handler, host, port, create_protocol=create_protocol, logger=logger,
                         compression=compression, origins=origins, extensions=extensions, subprotocols=subprotocols,
                         extra_headers=extra_headers, server_header=server_header, process_request=process_request,
                         select_subprotocol=select_subprotocol, open_timeout=open_timeout, ping_interval=ping_interval,
                         ping_timeout=ping_timeout, close_timeout=close_timeout, max_size=max_size, max_queue=max_queue,
                         read_limit=read_limit, write_limit=write_limit, **kwargs)
        legacy_recv: bool = kwargs.pop("legacy_recv", False)

        # Backwards compatibility: the loop parameter used to be supported.

        ws_server = WebSocketServer(logger=logger)

        secure = kwargs.get("ssl") is not None

        if compression == "deflate":
            extensions = enable_server_permessage_deflate(extensions)
        elif compression is not None:
            raise ValueError(f"unsupported compression: {compression}")

        if subprotocols is not None:
            validate_subprotocols(subprotocols)

        # 自定义protocol
        factory = functools.partial(
            create_protocol,
            # For backwards compatibility with 10.0 or earlier. Done here in
            # addition to WebSocketServerProtocol to trigger the deprecation
            # warning once per serve() call rather than once per connection.
            remove_path_argument(ws_handler),
            ws_server,
            host=host,
            port=port,
            ws_aio_server=ws_aio_server,
            secure=secure,
            open_timeout=open_timeout,
            ping_interval=ping_interval,
            ping_timeout=ping_timeout,
            close_timeout=close_timeout,
            max_size=max_size,
            max_queue=max_queue,
            read_limit=read_limit,
            write_limit=write_limit,
            loop=_loop,
            legacy_recv=legacy_recv,
            origins=origins,
            extensions=extensions,
            subprotocols=subprotocols,
            extra_headers=extra_headers,
            server_header=server_header,
            process_request=process_request,
            select_subprotocol=select_subprotocol,
            logger=logger,
        )

        if kwargs.pop("unix", False):
            path: Optional[str] = kwargs.pop("path", None)
            # unix_serve(path) must not specify host and port parameters.
            assert host is None and port is None
            create_server = functools.partial(
                _loop.create_unix_server, factory, path, **kwargs
            )
        else:
            create_server = functools.partial(
                _loop.create_server, factory, host, port, **kwargs
            )

        # This is a coroutine function.
        self._create_server = create_server
        self.ws_server = ws_server

    async def __aenter__(self) -> WebSocketServer:
        return await self

    async def __aexit__(
            self,
            exc_type: Optional[Type[BaseException]],
            exc_value: Optional[BaseException],
            traceback: Optional[TracebackType],
    ) -> None:
        self.ws_server.close()
        await self.ws_server.wait_closed()

    # await serve(...)

    def __await__(self) -> Generator[Any, None, WebSocketServer]:
        # Create a suitable iterator by calling __await__ on a coroutine.
        return self.__await_impl__().__await__()

    async def __await_impl__(self) -> WebSocketServer:
        server = await self._create_server()
        self.ws_server.wrap(server)
        return self.ws_server

    # yield from serve(...) - remove when dropping Python < 3.10

    __iter__ = __await__
