import {IoConsumer} from "../core/Types";
import {Session} from "../core/Session";
import {ChannelInternal} from "../core/Channel";

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
    connect(): ChannelInternal;

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
export abstract class ClientConnectorBase<T> implements ClientConnector{
    heartbeatHandler(): IoConsumer<Session> {
        throw new Error("Method not implemented.");
    }
    heartbeatInterval(): number {
        throw new Error("Method not implemented.");
    }
    autoReconnect(): boolean {
        throw new Error("Method not implemented.");
    }
    connect(): ChannelInternal {
        throw new Error("Method not implemented.");
    }
    close() {
        throw new Error("Method not implemented.");
    }

}