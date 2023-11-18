/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.client {
    /**
     * 客户端连接器
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface ClientConnector {
        /**
         * 心跳处理
         * @return {*}
         */
        heartbeatHandler(): org.noear.socketd.transport.core.HeartbeatHandler;

        /**
         * 心跳频率（单位：毫秒）
         * @return {number}
         */
        heartbeatInterval(): number;

        /**
         * 是否自动重连
         * @return {boolean}
         */
        autoReconnect(): boolean;

        /**
         * 连接
         * 
         * @return {*} 通道
         */
        connect(): org.noear.socketd.transport.core.ChannelInternal;
    }
}

