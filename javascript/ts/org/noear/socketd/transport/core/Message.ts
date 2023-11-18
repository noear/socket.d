/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 消息
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Message extends org.noear.socketd.transport.core.Entity {
        /**
         * 是否为请求
         * @return {boolean}
         */
        isRequest(): boolean;

        /**
         * 是否为订阅
         * @return {boolean}
         */
        isSubscribe(): boolean;

        /**
         * 获取消息流Id（用于消息交互、分片）
         * @return {string}
         */
        sid(): string;

        /**
         * 获取消息主题
         * @return {string}
         */
        topic(): string;

        /**
         * 获取消息实体（有时需要获取实体）
         * @return {*}
         */
        entity(): org.noear.socketd.transport.core.Entity;
    }
}

