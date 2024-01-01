"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ClientConnectorBase = void 0;
/**
 * 客户端连接器基类
 *
 * @author noear
 * @since 2.0
 */
class ClientConnectorBase {
    constructor(client) {
        this._client = client;
    }
    heartbeatHandler() {
        return this._client.getHeartbeatHandler();
    }
    heartbeatInterval() {
        return this._client.getHeartbeatInterval();
    }
    autoReconnect() {
        return this._client.getConfig().isAutoReconnect();
    }
}
exports.ClientConnectorBase = ClientConnectorBase;
