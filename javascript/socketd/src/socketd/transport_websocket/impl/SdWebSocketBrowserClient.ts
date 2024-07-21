import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl,
    SdWebSocketCloseEventImpl,
    SdWebSocketErrorEventImpl
} from "./SdWebSocket";
import {SocketAddress} from "../../transport/core/SocketAddress";
import {SocketD} from "../../SocketD";
import {Config} from "../../transport/core/Config";

export class SdWebSocketBrowserClient implements SdWebSocket {
    private _real: WebSocket;
    private _listener: SdWebSocketListener;

    constructor(url: string, config: Config, listener: SdWebSocketListener) {
        if (config.isUseSubprotocols()) {
            this._real = new WebSocket(url, SocketD.protocolName());
        } else {
            this._real = new WebSocket(url);
        }

        this._listener = listener;
        this._real.binaryType = "arraybuffer";

        this._real.onopen = this.onOpen.bind(this);
        this._real.onmessage = this.onMessage.bind(this);
        this._real.onclose = this.onClose.bind(this);
        this._real.onerror = this.onError.bind(this);
    }

    remoteAddress(): SocketAddress | null {
        return null;
    }

    localAddress(): SocketAddress | null {
        return null;
    }

    private _attachment: any;

    attachment() {
        return this._attachment;
    }

    attachmentPut(data: any) {
        this._attachment = data;
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
        let evt = new SdWebSocketEventImpl(this);
        this._listener.onOpen(evt);
    }

    onMessage(e: MessageEvent) {
        let evt = new SdWebSocketMessageEventImpl(this, e.data);
        this._listener.onMessage(evt);
    }

    onClose(e: CloseEvent) {
        let evt = new SdWebSocketCloseEventImpl(this);
        this._listener.onClose(evt);
    }

    onError(e) {
        let evt = new SdWebSocketErrorEventImpl(this, e);
        this._listener.onError(evt);
    }

    close(): void {
        this._real.close();
    }

    send(data: string | ArrayBuffer): void {
        this._real.send(data);
    }
}