import type {HandshakeInternal} from "./Handshake";
import type {MessageInternal} from "./Message";
import {EntityMetas} from "./Constants";

export class HandshakeDefault implements HandshakeInternal {
    private _source: MessageInternal
    private _url: URL
    private _version: string | null
    private _paramMap: Map<string, string>

    constructor(source: MessageInternal) {
        this._source = source;
        this._url = new URL(source.event());
        this._version = source.meta(EntityMetas.META_SOCKETD_VERSION);
        this._paramMap = new Map<string, string>();

        for (let [k, v] of this._url.searchParams) {
            this._paramMap.set(k, v);
        }
    }

    getSource(): MessageInternal {
        return this._source;
    }

    param(name: string): string | undefined{
        return this._paramMap.get(name);
    }

    paramMap(): Map<string, string> {
        return this._paramMap;
    }

    paramOrDefault(name: string, def: string): string {
        let res = this.param(name);
        return res ? res : def;
    }

    paramPut(name: string, value: string) {
        this._paramMap.set(name, value);
    }

    uri(): URL {
        return this._url;
    }

    version(): string | null{
        return this._version;
    }
}
