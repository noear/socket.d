/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.client {
    /**
     * 客户端（用于构建会话）
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Client {
        /**
         * 心跳
         * @param {*} handler
         * @return {*}
         */
        heartbeatHandler(handler: org.noear.socketd.transport.core.HeartbeatHandler): Client;

        /**
         * 配置
         * @param {*} consumer
         * @return {*}
         */
        config(consumer: org.noear.socketd.transport.client.ClientConfigHandler): Client;

        /**
         * 处理
         * @param {*} processor
         * @return {*}
         */
        process(processor: org.noear.socketd.transport.core.Processor): Client;

        /**
         * 监听
         * @param {*} listener
         * @return {*}
         */
        listen(listener: org.noear.socketd.transport.core.Listener): Client;

        /**
         * 打开会话
         * @return {*}
         */
        open(): org.noear.socketd.transport.core.Session;
    }
}

