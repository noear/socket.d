/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.listener {
    /**
     * 管道监听器
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class PipelineListener implements org.noear.socketd.transport.core.Listener {
        deque: Array<org.noear.socketd.transport.core.Listener>;

        /**
         * 前一个
         * @param {*} listener
         * @return {org.noear.socketd.transport.core.listener.PipelineListener}
         */
        public prev(listener: org.noear.socketd.transport.core.Listener): PipelineListener {
            /* addFirst */this.deque.unshift(listener);
            return this;
        }

        /**
         * 后一个
         * @param {*} listener
         * @return {org.noear.socketd.transport.core.listener.PipelineListener}
         */
        public next(listener: org.noear.socketd.transport.core.Listener): PipelineListener {
            /* addLast */(this.deque.push(listener)>0);
            return this;
        }

        /**
         * 打开时
         * 
         * @param {*} session 会话
         */
        public onOpen(session: org.noear.socketd.transport.core.Session) {
            for(let index = 0; index < this.deque.length; index++) {
                let listener = this.deque[index];
                {
                    listener.onOpen(session);
                }
            }
        }

        /**
         * 收到消息时
         * 
         * @param {*} session 会话
         * @param {*} message 消息
         */
        public onMessage(session: org.noear.socketd.transport.core.Session, message: org.noear.socketd.transport.core.Message) {
            for(let index = 0; index < this.deque.length; index++) {
                let listener = this.deque[index];
                {
                    listener.onMessage(session, message);
                }
            }
        }

        /**
         * 关闭时
         * 
         * @param {*} session 会话
         */
        public onClose(session: org.noear.socketd.transport.core.Session) {
            for(let index = 0; index < this.deque.length; index++) {
                let listener = this.deque[index];
                {
                    listener.onClose(session);
                }
            }
        }

        /**
         * 出错时
         * 
         * @param {*} session 会话
         * @param {Error} error   错误信息
         */
        public onError(session: org.noear.socketd.transport.core.Session, error: Error) {
            for(let index = 0; index < this.deque.length; index++) {
                let listener = this.deque[index];
                {
                    listener.onError(session, error);
                }
            }
        }

        constructor() {
            this.deque = <any>([]);
        }
    }
    PipelineListener["__class"] = "org.noear.socketd.transport.core.listener.PipelineListener";
    PipelineListener["__interfaces"] = ["org.noear.socketd.transport.core.Listener"];


}

