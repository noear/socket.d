import type {HandshakeInternal} from "./Handshake";
import type {MessageInternal} from "./Message";
import {EntityMetas} from "./EntityMetas";
import {StrUtils} from "../../utils/StrUtils";

export class HandshakeDefault implements HandshakeInternal {
    private _source: MessageInternal;
    private _url: string;
    private _path: string;
    private _version: string | null;
    private _paramMap: Map<string, string>;
    private _outMetaMap: Map<string, string>;

    constructor(source: MessageInternal) {
        let linkUrl = source.dataAsString();
        if (linkUrl == null || linkUrl == '') {
            //兼容旧版本（@deprecated 2.2.2）
            linkUrl = source.event();
        }

        this._source = source;
        this._url = linkUrl;
        this._version = source.meta(EntityMetas.META_SOCKETD_VERSION);
        this._outMetaMap = new Map<string, string>();
        this._paramMap = new Map<string, string>();
        let _uri = StrUtils.parseUri(linkUrl);

        if(_uri.path){
            //tcp://1.1.1.1 连接时，path 为空
            this._path = _uri.path;
        }else{
            this._path = "/";
        }

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

    getOutMetaMap(): Map<string, string> {
        return this._outMetaMap;
    }

    uri(): string {
        return this._url;
    }

    path(): string {
        return this._path;
    }

    version(): string | null {
        return this._version;
    }

    param(name: string): string | null {
        let tmp = this._paramMap.get(name);
        return tmp ? tmp : null;
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

    outMeta(name: string, value: string) {
        this._outMetaMap.set(name, value);
    }
}