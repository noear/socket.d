import {ConfigBase} from "../core/Config";

export class ClientConfig extends ConfigBase {
    //通讯架构（tcp, ws, udp）
    private _schema: string;

    //连接地址
    private _linkUrl: string;
    private _url: string;
    private _uri: URL;
    private _port: number;

    //心跳间隔（毫秒）
    private _heartbeatInterval: number;

    //连接越时（毫秒）
    private _connectTimeout: number;

    //是否自动重链
    private _autoReconnect: boolean;

    constructor(url: string) {
        super(true);

        //支持 sd: 开头的架构
        if (url.startsWith("sd:")) {
            url = url.substring(3);
        }

        this._url = url;
        this._uri = new URL(url);
        this._port = parseInt(this._uri.port);
        this._schema = this._uri.protocol;
        this._linkUrl = "sd:" + url;

        if (this._port < 0) {
            this._port = 8602;
        }

        this._connectTimeout = 10_000;
        this._heartbeatInterval = 20_000;

        this._autoReconnect = true;
    }


    /**
     * 获取通讯架构（tcp, ws, udp）
     */
    getSchema(): string {
        return this._schema;
    }


    /**
     * 获取连接地址
     */
    getUrl(): string {
        return this._url;
    }

    /**
     * 获取连接地址
     */
    getUri(): URL {
        return this._uri;
    }

    /**
     * 获取链接地址
     */
    getLinkUrl(): string {
        return this._linkUrl;
    }

    /**
     * 获取连接主机
     */
    getHost(): string {
        return this._uri.host;
    }

    /**
     * 获取连接端口
     */
    getPort(): number {
        return this._port;
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
    heartbeatInterval(heartbeatInterval: number): ClientConfig {
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

    toString(): string{
        return "ClientConfig{" +
            "schema='" + this._schema + '\'' +
            ", charset=" + this._charset +
            ", url='" + this._url + '\'' +
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