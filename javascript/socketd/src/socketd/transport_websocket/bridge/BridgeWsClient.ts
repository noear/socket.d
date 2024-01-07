export interface BridgeWsEvent {

}

export interface BridgeWsMsgEvent extends BridgeWsEvent {
    data(): any;
}

export interface BridgeWsCloseEvent extends BridgeWsEvent {

}

export interface BridgeWsClientConnector {
    onOpen(e: BridgeWsEvent): void;
    onMessage(e: BridgeWsMsgEvent): void;
    onClose(e: BridgeWsCloseEvent): void;
    onError(e: Error): void;
}

export interface BridgeWsClient {
    isConnecting(): boolean;
    isOpen(): boolean;
    isClosing(): boolean;
    isClosed(): boolean;
    close(): void;
    send(data: string | ArrayBuffer): void;
}


export class BridgeWsEventImpl implements BridgeWsEvent {
    constructor() {

    }
}

export class BridgeWsMsgEventImpl implements BridgeWsMsgEvent {
    private _data: any;
    constructor(data: any) {
        this._data = data;
    }
    data(): any {
        return this._data;
    }
}

export class BridgeWsCloseEventImpl implements BridgeWsCloseEvent {

}

