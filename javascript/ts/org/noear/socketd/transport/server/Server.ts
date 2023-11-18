/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.server {
    /**
     * 服务端
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Server {
        /**
         * 获取台头
         * 
         * @return {string}
         */
        title(): string;

        /**
         * 配置
         * @param {*} consumer
         * @return {*}
         */
        config(consumer?: any): any;

        /**
         * 处理
         * @param {*} processor
         * @return {*}
         */
        process(processor: org.noear.socketd.transport.core.Processor): Server;

        /**
         * 监听
         * @param {*} listener
         * @return {*}
         */
        listen(listener: org.noear.socketd.transport.core.Listener): Server;

        /**
         * 启动
         * @return {*}
         */
        start(): Server;

        /**
         * 停止
         */
        stop();
    }
}

