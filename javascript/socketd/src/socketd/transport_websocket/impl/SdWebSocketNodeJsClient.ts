import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketCloseEventImpl,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl,
    SdWebSocketErrorEventImpl
} from "./SdWebSocket";
import NodeWebSocket from 'ws';
import {SocketAddress} from "../../transport/core/SocketAddress";
import {SocketD} from "../../SocketD";
import {Config} from "../../transport/core/Config";

export class SdWebSocketNodeJsClient implements SdWebSocket {
    private _real: NodeWebSocket;
    private _listener: SdWebSocketListener;

    constructor(url: string, config:Config, listener: SdWebSocketListener) {
        if (config.isUseSubprotocols()) {
            this._real = new NodeWebSocket(url, SocketD.protocolName());
        } else {
            this._real = new NodeWebSocket(url);
        }

        this._listener = listener;
        this._real.binaryType = "arraybuffer";

        this._real.on('open', this.onOpen.bind(this));
        this._real.on('message', this.onMessage.bind(this));
        this._real.on('close', this.onClose.bind(this));
        this._real.on('error', this.onError.bind(this));
    }

    remoteAddress(): SocketAddress|null {
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
        let evt = new SdWebSocketEventImpl(this);
        this._listener.onOpen(evt);
    }

    onMessage(msg) {
        let evt = new SdWebSocketMessageEventImpl(this, msg);
        this._listener.onMessage(evt);
    }

    onClose() {
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
