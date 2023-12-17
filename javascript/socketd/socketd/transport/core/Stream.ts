import {MessageInternal} from "./Message";
import {Channel} from "./Channel";

export interface Stream {
    sid(): string;

    isSingle(): boolean;

    isDone(): boolean;

    timeout(): number;

    thenError(): Stream;
}

export interface SteamInternal extends Stream {
    onAccept(reply: MessageInternal, channel: Channel);

    onError(error: Error);
}

export abstract class StreamBase implements SteamInternal{
    onAccept(reply: MessageInternal, channel: Channel) {
        throw new Error("Method not implemented.");
    }
    onError(error: Error) {
        throw new Error("Method not implemented.");
    }
    sid(): string {
        throw new Error("Method not implemented.");
    }
    isSingle(): boolean {
        throw new Error("Method not implemented.");
    }
    isDone(): boolean {
        throw new Error("Method not implemented.");
    }
    timeout(): number {
        throw new Error("Method not implemented.");
    }
    thenError(): Stream {
        throw new Error("Method not implemented.");
    }

}

export class StreamManger {
    _streamMap: Map<string, SteamInternal>

    constructor() {
        this._streamMap = new Map<string, SteamInternal>();
    }

    getSteam(sid) {
        return this._streamMap.get(sid);
    }

    addSteam(sid, stream: SteamInternal) {
        this._streamMap.set(sid, stream);
    }

    removeSteam(sid) {
        this._streamMap.delete(sid);
    }
}
