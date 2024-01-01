"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ClientConfig = void 0;
const Config_1 = require("../core/Config");
class ClientConfig extends Config_1.ConfigBase {
    constructor(url) {
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
        this._connectTimeout = 10000;
        this._heartbeatInterval = 20000;
        this._autoReconnect = true;
    }
    /**
     * 获取通讯架构（tcp, ws, udp）
     */
    getSchema() {
        return this._schema;
    }
    /**
     * 获取连接地址
     */
    getUrl() {
        return this._url;
    }
    /**
     * 获取连接地址
     */
    getUri() {
        return this._uri;
    }
    /**
     * 获取链接地址
     */
    getLinkUrl() {
        return this._linkUrl;
    }
    /**
     * 获取连接主机
     */
    getHost() {
        return this._uri.host;
    }
    /**
     * 获取连接端口
     */
    getPort() {
        return this._port;
    }
    /**
     * 获取心跳间隔（单位毫秒）
     */
    getHeartbeatInterval() {
        return this._heartbeatInterval;
    }
    /**
     * 配置心跳间隔（单位毫秒）
     */
    heartbeatInterval(heartbeatInterval) {
        this._heartbeatInterval = heartbeatInterval;
        return this;
    }
    /**
     * 获取连接超时（单位毫秒）
     */
    getConnectTimeout() {
        return this._connectTimeout;
    }
    /**
     * 配置连接超时（单位毫秒）
     */
    connectTimeout(connectTimeout) {
        this._connectTimeout = connectTimeout;
        return this;
    }
    /**
     * 获取是否自动重链
     */
    isAutoReconnect() {
        return this._autoReconnect;
    }
    /**
     * 配置是否自动重链
     */
    autoReconnect(autoReconnect) {
        this._autoReconnect = autoReconnect;
        return this;
    }
    idleTimeout(idleTimeout) {
        if (this._autoReconnect == false) {
            //自动重链下，禁用 idleTimeout
            this._idleTimeout = (idleTimeout);
            return this;
        }
        else {
            this._idleTimeout = (0);
            return this;
        }
    }
    toString() {
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
exports.ClientConfig = ClientConfig;
