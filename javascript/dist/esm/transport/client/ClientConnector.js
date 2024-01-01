/**
 * 客户端连接器基类
 *
 * @author noear
 * @since 2.0
 */
export class ClientConnectorBase {
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
