/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 监听器
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Listener {
        /**
         * 打开时
         * 
         * @param {*} session 会话
         */
        onOpen(session: org.noear.socketd.transport.core.Session);

        /**
         * 收到消息时
         * 
         * @param {*} session 会话
         * @param {*} message 消息
         */
        onMessage(session: org.noear.socketd.transport.core.Session, message: org.noear.socketd.transport.core.Message);

        /**
         * 关闭时
         * 
         * @param {*} session 会话
         */
        onClose(session: org.noear.socketd.transport.core.Session);

        /**
         * 出错时
         * 
         * @param {*} session 会话
         * @param {Error} error   错误信息
         */
        onError(session: org.noear.socketd.transport.core.Session, error: Error);
    }
}

