import {IoConsumer} from "../core/Types";
import {Session} from "../core/Session";
import {ChannelInternal} from "../core/Channel";
import {ClientInternal} from "./Client";

/**
 * 客户端连接器
 *
 * @author noear
 * @since 2.0
 */
export interface ClientConnector {
    /**
     * 心跳处理
     */
    heartbeatHandler(): IoConsumer<Session>;

    /**
     * 心跳频率（单位：毫秒）
     */
    heartbeatInterval(): number;

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

    heartbeatHandler(): IoConsumer<Session> {
        return this._client.getHeartbeatHandler();
    }

    heartbeatInterval(): number {
        return this._client.getHeartbeatInterval();
    }

    autoReconnect(): boolean {
        return this._client.getConfig().isAutoReconnect();
    }

    abstract connect(): Promise<ChannelInternal>;

    abstract close();
}