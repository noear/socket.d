import {
    BridgeWsClient,
    BridgeWsClientConnector,
    BridgeWsCloseEventImpl,
    BridgeWsEventImpl,
    BridgeWsMsgEventImpl
} from "../BridgeWsClient";
import {Logger} from "../../../utils/LogUtils";
import NodeWebSocket from 'ws';

export class NodeWsClientImpl implements BridgeWsClient {
    private _real: NodeWebSocket;
    private _connector: BridgeWsClientConnector;
    private _logger: Logger;

    constructor(url: string, connector: BridgeWsClientConnector) {
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
        let evt = new BridgeWsEventImpl();
        // TODO event细节待完善
        this._connector.onOpen(evt);
    }

    onMessage(msg) {
        let evt = new BridgeWsMsgEventImpl(msg);
        // TODO event细节待完善
        this._connector.onMessage(evt);
    }

    onClose() {
        this._logger.debug("onClose");
        let evt = new BridgeWsCloseEventImpl();
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
