import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl,
    SdWebSocketCloseEventImpl
} from "./SdWebSocket";

export class SdWebSocketBrowserImpl implements SdWebSocket {
    private _real: WebSocket;
    private _listener: SdWebSocketListener;
    constructor(url: string, listener: SdWebSocketListener) {
        this._real = new WebSocket(url);
        this._listener = listener;
        this._real.binaryType = "arraybuffer";

        this._real.onopen = this.onOpen.bind(this);
        this._real.onmessage = this.onMessage.bind(this);
        this._real.onclose = this.onClose.bind(this);
        this._real.onerror = this.onError.bind(this);
    }

    isConnecting(): boolean {
        return this._real.readyState == WebSocket.CONNECTING;
    }

    isClosed(): boolean {
        return this._real.readyState == WebSocket.CLOSED;
    }

    isClosing(): boolean {
        return this._real.readyState == WebSocket.CLOSING;
    }

    isOpen(): boolean {
        return this._real.readyState == WebSocket.OPEN;
    }

    onOpen(e: Event) {
        let evt = new SdWebSocketEventImpl();
        this._listener.onOpen(evt);
    }

    onMessage(e: MessageEvent) {
        let evt = new SdWebSocketMessageEventImpl(e.data);
        this._listener.onMessage(evt);
    }

    onClose(e: CloseEvent) {
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
