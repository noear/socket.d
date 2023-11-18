/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 帧（帧[消息[实体]]）
     * 
     * @author noear
     * @since 2.0
     * @param {org.noear.socketd.transport.core.Flag} flag
     * @param {*} message
     * @class
     */
    export class Frame {
        /*private*/ flag: org.noear.socketd.transport.core.Flag;

        /*private*/ message: org.noear.socketd.transport.core.Message;

        public constructor(flag: org.noear.socketd.transport.core.Flag, message: org.noear.socketd.transport.core.Message) {
            if (this.flag === undefined) { this.flag = null; }
            if (this.message === undefined) { this.message = null; }
            this.flag = flag;
            this.message = message;
        }

        /**
         * 标志
         * 
         * @return {org.noear.socketd.transport.core.Flag}
         */
        public getFlag(): org.noear.socketd.transport.core.Flag {
            return this.flag;
        }

        /**
         * 消息
         * 
         * @return {*}
         */
        public getMessage(): org.noear.socketd.transport.core.Message {
            return this.message;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "Frame{flag=" + this.flag + ", message=" + this.message + '}';
        }
    }
    Frame["__class"] = "org.noear.socketd.transport.core.Frame";

}

