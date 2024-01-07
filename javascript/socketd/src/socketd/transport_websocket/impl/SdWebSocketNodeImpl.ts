import {
    SdWebSocket,
    SdWebSocketListener,
    SdWebSocketCloseEventImpl,
    SdWebSocketEventImpl,
    SdWebSocketMessageEventImpl
} from "./SdWebSocket";
import {Logger} from "../../utils/LogUtils";
import NodeWebSocket from 'ws';

export class SdWebSocketNodeImpl implements SdWebSocket {
    private _real: NodeWebSocket;
    private _connector: SdWebSocketListener;
    private _logger: Logger;

    constructor(url: string, connector: SdWebSocketListener) {
        this._logger = new Logger("BridgeWsClientImpl.node");
        this._logger.debug("实例化...");
        this._real = new NodeWebSocket(url);
        this._connector = connector;
        this._real.binaryType = "arraybuffer";
        this._real.on('open', this.onOpen.bind(this));
        this._real.on('message', this.onMessage.bind(this));
        this._real.on('close', this.onClose.bind(this));
        this._real.on('error', this.onError.bind(this));
    }

    close(): void {
    }

    isClosed(): boolean {
        return false;
    }

    isClosing(): boolean {
        return false;
    }

    isConnecting(): boolean {
        return false;
    }

    isOpen(): boolean {
        return false;
    }

    onOpen() {
        this._logger.debug("onOpen");
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
        this._logger.debug("onClose");
        let evt = new SdWebSocketCloseEventImpl();
        // TODO event细节待完善
        this._connector.onClose(evt);
    }

    onError(e) {
        this._logger.debug("onError", e);
        this._connector.onError(e);
    }

    send(data: string | ArrayBuffer): void {
        this._real.send(data);
    }

}
