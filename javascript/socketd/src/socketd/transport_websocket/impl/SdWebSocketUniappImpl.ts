import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl,
    SdWebSocketCloseEventImpl, SdWebSocketState
} from "./SdWebSocket";

export class SdWebSocketUniappImpl implements SdWebSocket {
    private _real: any;
    private _state: SdWebSocketState;
    private _listener: SdWebSocketListener;

    constructor(url: string, listener: SdWebSocketListener) {
        this._state = SdWebSocketState.CONNECTING;
        // @ts-ignore
        this._real = uni.connectSocket({url: url, success:(r)=>{}});//SocketTask
        this._listener = listener;

        this._real.onOpen(this.onOpen.bind(this));
        this._real.onMessage(this.onMessage.bind(this));
        this._real.onClose(this.onClose.bind(this));
        this._real.onError(this.onError.bind(this));
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
        let evt = new SdWebSocketEventImpl();
        this._state = SdWebSocketState.OPEN;
        this._listener.onOpen(evt);
    }

    onMessage(e: MessageEvent) {
        let evt = new SdWebSocketMessageEventImpl(e.data);
        this._listener.onMessage(evt);
    }

    onClose(e: CloseEvent) {
        let evt = new SdWebSocketCloseEventImpl();
        this._state = SdWebSocketState.CLOSED;
        this._listener.onClose(evt);
    }

    onError(e) {
        this._listener.onError(e);
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
