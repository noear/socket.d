/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 默认心跳处理默认实现
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class HeartbeatHandlerDefault {
        /**
         * 心跳处理
         * @param {*} session
         */
        public heartbeat(session: org.noear.socketd.transport.core.Session) {
            session.sendPing();
        }

        constructor() {
        }
    }
    HeartbeatHandlerDefault["__class"] = "org.noear.socketd.transport.core.internal.HeartbeatHandlerDefault";
    HeartbeatHandlerDefault["__interfaces"] = ["org.noear.socketd.transport.core.HeartbeatHandler"];


}

