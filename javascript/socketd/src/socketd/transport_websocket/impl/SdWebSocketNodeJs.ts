
import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketCloseEventImpl,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl, SdWebSocketErrorEventImpl, SdWebSocketPingEventImpl, SdWebSocketPongEventImpl
} from "./SdWebSocket";
import NodeWebSocket from 'ws';
import {SocketAddress} from "../../transport/core/SocketAddress";
import {IncomingMessage} from "http";
import {ChannelDefault} from "../../transport/core/impl/ChannelDefault";
import {SdWebSocketServerListener} from "../WsServer";
import {RunUtils} from "../../utils/RunUtils";
import {ServerConfig} from "../../transport/server/ServerConfig";

export class SdWebSocketNodeJs implements SdWebSocket {
    private _config:ServerConfig;
    private _real: NodeWebSocket;
    private _listener: SdWebSocketListener;
    private _remoteAddress : SocketAddress|null;
    private _localAddress : SocketAddress|null;
    private _lastPongTime: number = 0;
    //心跳调度
    private _heartbeatScheduledFuture: any;

    constructor(config: ServerConfig,real: NodeWebSocket, req:IncomingMessage, listener: SdWebSocketServerListener) {
        this._config = config;
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

        this._real.on('message', this.onMessage.bind(this));
        this._real.on('close', this.onClose.bind(this));
        this._real.on('error', this.onError.bind(this));
        this._real.on('ping', this.onPing.bind(this));
        this._real.on('pong', this.onPong.bind(this));
        this.onOpen();

        this._lastPongTime = new Date().getTime();
        this._heartbeatScheduledFuture = setInterval(() => {
            this.doPing();
        }, 20_000);
    }

    private doPing(){
        if(new Date().getTime() -  this._lastPongTime > this._config.getIdleTimeout()){
            //
            this._real.close();
            return;
        }

        this._real.ping();
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
        RunUtils.runAndTry(() => clearInterval(this._heartbeatScheduledFuture));
        let evt = new SdWebSocketCloseEventImpl(this);
        this._listener.onClose(evt);
    }

    onError(e) {
        let evt = new SdWebSocketErrorEventImpl(this, e);
        this._listener.onError(evt);
    }

    onPing(){
        let evt = new SdWebSocketPingEventImpl(this);
        this._listener.onPing(evt);
    }

    onPong(){
        this._lastPongTime = new Date().getTime();

        let evt = new SdWebSocketPongEventImpl(this);
        this._listener.onPong(evt);
    }

    close(): void {
        this._real.close();
    }

    send(data: string | ArrayBuffer): void {
        this._real.send(data);
    }
}