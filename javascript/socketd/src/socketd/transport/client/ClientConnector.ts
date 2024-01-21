import type {IoConsumer} from "../core/Typealias";
import type {Session} from "../core/Session";
import type {ChannelInternal} from "../core/Channel";
import type {ClientInternal} from "./Client";
import {Config} from "../core/Config";

/**
 * 客户端连接器
 *
 * @author noear
 * @since 2.0
 */
export interface ClientConnector {
    /**
     * 获取配置
     * */
    getConfig(): Config;

    /**
     * 获取心跳处理
     */
    getHeartbeatHandler(): IoConsumer<Session>;

    /**
     * 获取心跳频率（单位：毫秒）
     */
    getHeartbeatInterval(): number;

    /**
     * 是否自动重连
     */
    autoReconnect(): boolean;

    /**
     * 连接
     *
     * @return 通道
     */
    connect(): Promise<ChannelInternal>;

    /**
     * 关闭
     */
    close();
}

/**
 * 客户端连接器基类
 *
 * @author noear
 * @since 2.0
 */
export abstract class ClientConnectorBase<T extends ClientInternal> implements ClientConnector {
    protected _client: T;

    constructor(client: T) {
        this._client = client;
    }

    getConfig(): Config {
        return this._client.getConfig();
    }

    getHeartbeatHandler(): IoConsumer<Session> {
        return this._client.getHeartbeatHandler();
    }

    getHeartbeatInterval(): number {
        return this._client.getHeartbeatInterval();
    }

    autoReconnect(): boolean {
        return this._client.getConfig().isAutoReconnect();
    }

    abstract connect(): Promise<ChannelInternal>;

    abstract close();
}