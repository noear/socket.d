import {SocketAddress} from "../../transport/core/SocketAddress";

export interface SdWebSocket {
    isConnecting(): boolean;
    isOpen(): boolean;
    isClosing(): boolean;
    isClosed(): boolean;
    close(): void;
    send(data: string | ArrayBuffer): void;

    attachment(): any;
    attachmentPut(data: any);
    remoteAddress(): SocketAddress|null;
    localAddress(): SocketAddress|null;
}

export interface SdWebSocketListener {
    onOpen(e: SdWebSocketEvent): void;
    onMessage(e: SdWebSocketMessageEvent): void;
    onClose(e: SdWebSocketCloseEvent): void;
    onError(e: SdWebSocketErrorEvent): void;
    onPing(e: SdWebSocketPingEvent): void;
    onPong(e: SdWebSocketPongEvent): void;
}

export enum SdWebSocketState {
    CONNECTING = 0,
    OPEN = 1,
    CLOSING = 2,
    CLOSED = 3
}

export interface SdWebSocketEvent {
    socket():SdWebSocket;
}

export interface SdWebSocketMessageEvent extends SdWebSocketEvent {
    socket():SdWebSocket;
    data(): any;
}

export interface SdWebSocketCloseEvent extends SdWebSocketEvent {
    socket():SdWebSocket;
}

export interface SdWebSocketErrorEvent extends SdWebSocketEvent {
    socket():SdWebSocket;
    error(): any;
}

export interface SdWebSocketPingEvent extends SdWebSocketEvent {
    socket():SdWebSocket;
}

export interface SdWebSocketPongEvent extends SdWebSocketEvent {
    socket():SdWebSocket;
}

export class SdWebSocketEventImpl implements SdWebSocketEvent {
    private _socket:SdWebSocket;
    constructor(socket:SdWebSocket) {
        this._socket = socket;
    }

    socket(): SdWebSocket {
        return this._socket;
    }

}

export class SdWebSocketMessageEventImpl implements SdWebSocketMessageEvent {
    private _socket:SdWebSocket;
    private _data: any;
    constructor(socket:SdWebSocket,data: any) {
        this._socket = socket;
        this._data = data;
    }

    socket(): SdWebSocket {
        return this._socket;
    }
    data(): any {
        return this._data;
    }
}

export class SdWebSocketCloseEventImpl implements SdWebSocketCloseEvent {
    private _socket: SdWebSocket;

    constructor(socket: SdWebSocket) {
        this._socket = socket;
    }

    socket(): SdWebSocket {
        return this._socket;
    }
}

export class SdWebSocketErrorEventImpl implements SdWebSocketErrorEvent {
    private _socket: SdWebSocket;
    private _error: any;

    constructor(socket: SdWebSocket, error: any) {
        this._socket = socket;
        this._error = error;
    }

    socket(): SdWebSocket {
        return this._socket;
    }

    error(): any {
        return this._error;
    }
}

export class SdWebSocketPingEventImpl implements SdWebSocketPingEvent {
    private _socket: SdWebSocket;

    constructor(socket: SdWebSocket) {
        this._socket = socket;
    }

    socket(): SdWebSocket {
        return this._socket;
    }
}

export class SdWebSocketPongEventImpl implements SdWebSocketPongEvent {
    private _socket: SdWebSocket;

    constructor(socket: SdWebSocket) {
        this._socket = socket;
    }

    socket(): SdWebSocket {
        return this._socket;
    }
}

