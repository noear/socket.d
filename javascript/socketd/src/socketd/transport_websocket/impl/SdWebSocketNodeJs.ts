
import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketCloseEventImpl,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl, SdWebSocketErrorEventImpl
} from "./SdWebSocket";
import NodeWebSocket from 'ws';
import {SocketAddress} from "../../transport/core/SocketAddress";
import {IncomingMessage} from "http";
import {ChannelDefault} from "../../transport/core/ChannelDefault";
import {SdWebSocketServerListener} from "../WsServer";

export class SdWebSocketNodeJs implements SdWebSocket {
    private _real: NodeWebSocket;
    private _listener: SdWebSocketListener;
    private _remoteAddress : SocketAddress|null;
    private _localAddress : SocketAddress|null;

    constructor(real: NodeWebSocket, req:IncomingMessage, listener: SdWebSocketServerListener) {
        this._real = real;
        this._listener = listener;
        this._real.binaryType = "arraybuffer";

        if(req.socket.remoteAddress) {
            this._remoteAddress = new SocketAddress(req.socket.remoteAddress!, req.socket.remoteFamily!, req.socket.remotePort!)
        }else{
            this._remoteAddress = null;
        }

        if(req.socket.localAddress){
            this._localAddress = new SocketAddress(req.socket.localAddress!, req.socket.localFamily!, req.socket.localPort!)
        }else{
            this._localAddress = null;
        }

        const channl = new ChannelDefault(this, listener.getServer());
        this.attachmentPut(channl);

        this._real.on('open', this.onOpen.bind(this));
        this._real.on('message', this.onMessage.bind(this));
        this._real.on('close', this.onClose.bind(this));
        this._real.on('error', this.onError.bind(this));
    }

    remoteAddress(): SocketAddress|null {
        return this._remoteAddress;
    }
    localAddress(): SocketAddress | null {
        return this._localAddress;
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