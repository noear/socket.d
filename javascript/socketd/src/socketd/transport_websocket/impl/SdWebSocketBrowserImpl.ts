import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl,
    SdWebSocketCloseEventImpl
} from "./SdWebSocket";
import {Logger} from "../../utils/LogUtils";

export class SdWebSocketBrowserImpl implements SdWebSocket {
    private _real: WebSocket;
    private _connector: SdWebSocketListener;
    private _logger: Logger;
    constructor(url: string, connector: SdWebSocketListener) {
        this._logger = new Logger("BridgeWsClientImpl.browser");
        this._logger.debug("实例化...");
        this._real = new WebSocket(url);
        this._connector = connector;
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
        this._logger.debug("onOpen", e);
        let evt = new SdWebSocketEventImpl();
        // TODO event细节待完善
        this._connector.onOpen(evt);
    }

    onMessage(e: MessageEvent) {
        let evt = new SdWebSocketMessageEventImpl(e.data);
        // TODO event细节待完善
        this._connector.onMessage(evt);
    }

    onClose(e: CloseEvent) {
        this._logger.debug("onClose", e);
        let evt = new SdWebSocketCloseEventImpl();
        // TODO event细节待完善
        this._connector.onClose(evt);
    }

    onError(e) {
        this._logger.debug("onError", e);
        this._connector.onError(e);
    }

    close(): void {
        this._real.close();
    }

    send(data: string | ArrayBuffer): void {
        this._real.send(data);
    }
}
