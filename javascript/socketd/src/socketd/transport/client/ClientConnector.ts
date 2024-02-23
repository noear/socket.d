import type {IoConsumer} from "../core/Typealias";
import type {Session} from "../core/Session";
import type {ChannelInternal} from "../core/Channel";
import type {ClientInternal} from "./Client";
import {Config} from "../core/Config";
import {ClientConfig} from "./ClientConfig";

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
    getConfig(): ClientConfig;

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

    getConfig(): ClientConfig {
        return this._client.getConfig();
    }

    autoReconnect(): boolean {
        return this._client.getConfig().isAutoReconnect();
    }

    abstract connect(): Promise<ChannelInternal>;

    abstract close();
}