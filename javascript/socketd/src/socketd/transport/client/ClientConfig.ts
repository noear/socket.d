import {ConfigBase} from "../core/Config";
import {StrUtils} from "../../utils/StrUtils";

export class ClientConfig extends ConfigBase {
    //协议架构（tcp, ws, udp, ...）
    private readonly _schema: string;
    private readonly _schemaCleaned: string;

    //连接地址
    private readonly _linkUrl: string;
    private readonly _url: string;
    private readonly _host: string;
    private readonly _port: number;
    private _metaMap: Map<string, string> = new Map<string, string>();

    //心跳间隔（毫秒）
    private _heartbeatInterval: number;

    //连接越时（毫秒）
    private _connectTimeout: number;

    //是否自动重链
    private _autoReconnect: boolean;

    constructor(url: string) {
        super(true);

        const idx = url.indexOf("://");
        if (idx < 2) {
            throw new Error("The serverUrl invalid: " + url);
        }

        this._schema = url.substring(0, idx);

        //支持 sd: 开头的架构
        if (url.startsWith("sd:")) {
            url = url.substring(3);
        }

        this._url = url;
        this._linkUrl = "sd:" + url;

        let _uri = StrUtils.parseUri(url);

        this._host = _uri.host;
        this._port = parseInt(_uri.port);
        this._schemaCleaned = _uri.protocol;

        if (this._port < 0) {
            this._port = 8602;
        }

        this._connectTimeout = 10_000;
        this._heartbeatInterval = 20_000;

        this._autoReconnect = true;
    }


    /**
     * 获取协议架构（tcp, ws, udp）
     */
    getSchema(): string {
        return this._schema;
    }

    /**
     * 获取链接地址
     */
    getLinkUrl(): string {
        return this._linkUrl;
    }

    /**
     * 获取连接地址
     */
    getUrl(): string {
        return this._url;
    }

    /**
     * 获取连接主机
     */
    getHost(): string {
        return this._host;
    }

    /**
     * 获取连接端口
     */
    getPort(): number {
        return this._port;
    }

    /**
     * 获取连接元信息字典
     */
    getMetaMap(): Map<string, string> {
        return this._metaMap;
    }

    metaPut(name: string, val: string): this {
        this._metaMap.set(name, val);
        return this;
    }


    /**
     * 获取心跳间隔（单位毫秒）
     */
    getHeartbeatInterval(): number {
        return this._heartbeatInterval;
    }

    /**
     * 配置心跳间隔（单位毫秒）
     */
    heartbeatInterval(heartbeatInterval: number): this {
        this._heartbeatInterval = heartbeatInterval;
        return this;
    }

    /**
     * 获取连接超时（单位毫秒）
     */
    getConnectTimeout(): number {
        return this._connectTimeout;
    }

    /**
     * 配置连接超时（单位毫秒）
     */
    connectTimeout(connectTimeout: number): this {
        this._connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 获取是否自动重链
     */
    isAutoReconnect(): boolean {
        return this._autoReconnect;
    }

    /**
     * 配置是否自动重链
     */
    autoReconnect(autoReconnect: boolean): this {
        this._autoReconnect = autoReconnect;
        return this;
    }

    idleTimeout(idleTimeout: number): this {
        if (this._autoReconnect == false) {
            //自动重链下，禁用 idleTimeout
            this._idleTimeout = (idleTimeout);
            return this;
        } else {
            this._idleTimeout = (0);
            return this;
        }
    }

    toString(): string {
        return "ClientConfig{" +
            "schema='" + this._schemaCleaned + '\'' +
            ", charset=" + this._charset +
            ", url='" + this._url + '\'' +
            ", ioThreads=" + this._ioThreads +
            ", codecThreads=" + this._codecThreads +
            ", exchangeThreads=" + this._exchangeThreads +
            ", heartbeatInterval=" + this._heartbeatInterval +
            ", connectTimeout=" + this._connectTimeout +
            ", idleTimeout=" + this._idleTimeout +
            ", requestTimeout=" + this._requestTimeout +
            ", readBufferSize=" + this._readBufferSize +
            ", writeBufferSize=" + this._writeBufferSize +
            ", autoReconnect=" + this._autoReconnect +
            ", maxUdpSize=" + this._maxUdpSize +
            '}';
    }
}