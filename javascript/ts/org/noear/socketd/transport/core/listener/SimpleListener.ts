/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.listener {
    /**
     * 简单监听器（一般用于占位）
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class SimpleListener implements org.noear.socketd.transport.core.Listener {
        /**
         * 打开时
         * 
         * @param {*} session 会话
         */
        public onOpen(session: org.noear.socketd.transport.core.Session) {
        }

        /**
         * 收到消息时
         * 
         * @param {*} session 会话
         * @param {*} message 消息
         */
        public onMessage(session: org.noear.socketd.transport.core.Session, message: org.noear.socketd.transport.core.Message) {
        }

        /**
         * 关闭时
         * 
         * @param {*} session 会话
         */
        public onClose(session: org.noear.socketd.transport.core.Session) {
        }

        /**
         * 出错时
         * 
         * @param {*} session 会话
         * @param {Error} error   错误信息
         */
        public onError(session: org.noear.socketd.transport.core.Session, error: Error) {
        }

        constructor() {
        }
    }
    SimpleListener["__class"] = "org.noear.socketd.transport.core.listener.SimpleListener";
    SimpleListener["__interfaces"] = ["org.noear.socketd.transport.core.Listener"];


}

