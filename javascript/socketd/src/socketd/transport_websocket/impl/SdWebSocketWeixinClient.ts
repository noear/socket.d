import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl,
    SdWebSocketCloseEventImpl,
    SdWebSocketState,
    SdWebSocketErrorEventImpl
} from "./SdWebSocket";
import {SocketAddress} from "../../transport/core/SocketAddress";
import {SocketD} from "../../SocketD";
import {Config} from "../../transport/core/Config";

export class SdWebSocketWeixinClient implements SdWebSocket {
    private _real: any;
    private _state: SdWebSocketState;
    private _listener: SdWebSocketListener;

    constructor(url: string, config: Config, listener: SdWebSocketListener) {
        this._state = SdWebSocketState.CONNECTING;
        if (config.isUseSubprotocols()) {
            // @ts-ignore
            this._real = wx.connectSocket({url: url, protocols: [SocketD.protocolName()]});//SocketTask
        } else {
            // @ts-ignore
            this._real = wx.connectSocket({url: url});//SocketTask
        }

        this._listener = listener;
        this._real.binaryType = "arraybuffer";

        this._real.onOpen(this.onOpen.bind(this));
        this._real.onMessage(this.onMessage.bind(this));
        this._real.onClose(this.onClose.bind(this));
        this._real.onError(this.onError.bind(this));
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
        return this._state == SdWebSocketState.CONNECTING;
    }

    isClosed(): boolean {
        return this._state == SdWebSocketState.CLOSED;
    }

    isClosing(): boolean {
        return this._state == SdWebSocketState.CLOSING;
    }

    isOpen(): boolean {
        return this._state == SdWebSocketState.OPEN;
    }

    onOpen(e: Event) {
        let evt = new SdWebSocketEventImpl(this);
        this._state = SdWebSocketState.OPEN;
        this._listener.onOpen(evt);
    }

    onMessage(e: MessageEvent) {
        let evt = new SdWebSocketMessageEventImpl(this, e.data);
        this._listener.onMessage(evt);
    }

    onClose(e: CloseEvent) {
        let evt = new SdWebSocketCloseEventImpl(this);
        this._state = SdWebSocketState.CLOSED;
        this._listener.onClose(evt);
    }

    onError(e) {
        let evt = new SdWebSocketErrorEventImpl(this, e);
        this._listener.onError(evt);
    }

    close(): void {
        this._state = SdWebSocketState.CLOSING;
        this._real.close({
            complete: () => {
                this._state = SdWebSocketState.CLOSED;
            }
        });
    }

    send(data: string | ArrayBuffer): void {
        this._real.send({data: data});
    }
}