export interface SdWebSocketEvent {

}

export interface SdWebSocketMessageEvent extends SdWebSocketEvent {
    data(): any;
}

export interface SdWebSocketCloseEvent extends SdWebSocketEvent {

}

export interface SdWebSocketListener {
    onOpen(e: SdWebSocketEvent): void;
    onMessage(e: SdWebSocketMessageEvent): void;
    onClose(e: SdWebSocketCloseEvent): void;
    onError(e: Error): void;
}

export interface SdWebSocket {
    isConnecting(): boolean;
    isOpen(): boolean;
    isClosing(): boolean;
    isClosed(): boolean;
    close(): void;
    send(data: string | ArrayBuffer): void;
}


export class SdWebSocketEventImpl implements SdWebSocketEvent {
    constructor() {

    }
}

export class SdWebSocketMessageEventImpl implements SdWebSocketMessageEvent {
    private _data: any;
    constructor(data: any) {
        this._data = data;
    }
    data(): any {
        return this._data;
    }
}

export class SdWebSocketCloseEventImpl implements SdWebSocketCloseEvent {

}

