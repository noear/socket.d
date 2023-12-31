import { EntityMetas } from "./Constants";
export class HandshakeDefault {
    constructor(source) {
        this._source = source;
        this._url = new URL(source.event());
        this._version = source.meta(EntityMetas.META_SOCKETD_VERSION);
        this._paramMap = new Map();
        for (const [k, v] of this._url.searchParams) {
            this._paramMap.set(k, v);
        }
    }
    getSource() {
        return this._source;
    }
    param(name) {
        return this._paramMap.get(name);
    }
    paramMap() {
        return this._paramMap;
    }
    paramOrDefault(name, def) {
        const tmp = this.param(name);
        return tmp ? tmp : def;
    }
    paramPut(name, value) {
        this._paramMap.set(name, value);
    }
    uri() {
        return this._url;
    }
    version() {
        return this._version;
    }
}
