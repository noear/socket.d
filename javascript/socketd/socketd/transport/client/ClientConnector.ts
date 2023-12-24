import {IoConsumer} from "../core/Types";
import {Session} from "../core/Session";
import {ChannelInternal} from "../core/Channel";

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