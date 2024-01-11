import type {HandshakeInternal} from "./Handshake";
import type {MessageInternal} from "./Message";
import {EntityMetas} from "./Constants";
import {StrUtils} from "../../utils/StrUtils";

export class HandshakeDefault implements HandshakeInternal {
    private _source: MessageInternal;
    private _url: string;
    private _path: string;
    private _version: string | null;
    private _paramMap: Map<string, string>;

    constructor(source: MessageInternal) {
        let linkUrl = source.dataAsString();
        if (linkUrl == null || linkUrl == '') {
            //兼容旧版本（@deprecated 2.2.2）
            linkUrl = source.event();
        }

        this._source = source;
        this._url = linkUrl;
        this._version = source.meta(EntityMetas.META_SOCKETD_VERSION);
        this._paramMap = new Map<string, string>();
        let _uri = StrUtils.parseUri(linkUrl);
        this._path = _uri.path;

        //添加连接参数
        const queryStr = _uri.query;
        if (queryStr) {
            for (const kvStr of queryStr.split("&")) {
                const idx = kvStr.indexOf('=');
                if (idx > 0) {
                    this._paramMap.set(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                }
            }
        }

        //添加元信息参数
        source.metaMap().forEach((val, key, p) => {
            this._paramMap.set(key, val);
        });
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
        const tmp = this.param(name);
        return tmp ? tmp : def;
    }

    paramPut(name: string, value: string) {
        this._paramMap.set(name, value);
    }

    uri(): string {
        return this._url;
    }

    path(): string {
        return this._path;
    }

    version(): string | null{
        return this._version;
    }
}
