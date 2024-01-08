import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketCloseEventImpl,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl
} from "./SdWebSocket";
import NodeWebSocket from 'ws';

export class SdWebSocketNodeJsImpl implements SdWebSocket {
    private _real: NodeWebSocket;
    private _listener: SdWebSocketListener;

    constructor(url: string, listener: SdWebSocketListener) {
        this._real = new NodeWebSocket(url);
        this._listener = listener;
        this._real.binaryType = "arraybuffer";

        this._real.on('open', this.onOpen.bind(this));
        this._real.on('message', this.onMessage.bind(this));
        this._real.on('close', this.onClose.bind(this));
        this._real.on('error', this.onError.bind(this));
    }

    isClosed(): boolean {
        return this._real.readyState == NodeWebSocket.CLOSED;
    }

    isClosing(): boolean {
        return this._real.readyState == NodeWebSocket.CLOSING;
    }

    isConnecting(): boolean {
        return this._real.readyState == NodeWebSocket.CONNECTING;
    }

    isOpen(): boolean {
        return this._real.readyState == NodeWebSocket.OPEN;
    }

    onOpen() {
        let evt = new SdWebSocketEventImpl();
        this._listener.onOpen(evt);
    }

    onMessage(msg) {
        let evt = new SdWebSocketMessageEventImpl(msg);
        this._listener.onMessage(evt);
    }

    onClose() {
        let evt = new SdWebSocketCloseEventImpl();
        this._listener.onClose(evt);
    }

    onError(e) {
        this._listener.onError(e);
    }

    close(): void {
        this._real.close();
    }

    send(data: string | ArrayBuffer): void {
        this._real.send(data);
    }
}
