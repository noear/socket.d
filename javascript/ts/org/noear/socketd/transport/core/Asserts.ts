/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 断言
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class Asserts {
        /**
         * 断言关闭
         * @param {*} channel
         */
        public static assertClosed(channel: org.noear.socketd.transport.core.Channel) {
            if (channel != null && channel.isClosed() > 0){
                throw new org.noear.socketd.exception.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
            }
        }

        /**
         * 断言关闭
         * @param {*} channel
         */
        public static assertClosedByUser(channel: org.noear.socketd.transport.core.Channel) {
            if (channel != null && channel.isClosed() === org.noear.socketd.transport.core.Constants.CLOSE3_USER){
                throw new org.noear.socketd.exception.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
            }
        }

        /**
         * 断言null
         * 
         * @param {*} val
         * @param {string} name
         */
        public static assertNull(val: any, name: string) {
            if (val == null){
                throw Object.defineProperty(new Error("The argument cannot be null: " + name), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.IllegalArgumentException','java.lang.Exception'] });
            }
        }
    }
    Asserts["__class"] = "org.noear.socketd.transport.core.Asserts";

}

