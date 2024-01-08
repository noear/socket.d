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
    private _connector: SdWebSocketListener;

    constructor(url: string, connector: SdWebSocketListener) {
        this._real = new NodeWebSocket(url);
        this._connector = connector;
        this._real.binaryType = "arraybuffer";
        this._real.on('open', this.onOpen.bind(this));
        this._real.on('message', this.onMessage.bind(this));
        this._real.on('close', this.onClose.bind(this));
        this._real.on('error', this.onError.bind(this));
    }

    close(): void {
        this._real.close();
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
        // TODO event细节待完善
        this._connector.onOpen(evt);
    }

    onMessage(msg) {
        let evt = new SdWebSocketMessageEventImpl(msg);
        // TODO event细节待完善
        this._connector.onMessage(evt);
    }

    onClose() {
        let evt = new SdWebSocketCloseEventImpl();
        // TODO event细节待完善
        this._connector.onClose(evt);
    }

    onError(e) {
        this._connector.onError(e);
    }

    send(data: string | ArrayBuffer): void {
        this._real.send(data);
    }

}
