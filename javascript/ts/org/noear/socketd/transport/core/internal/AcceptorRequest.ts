/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 请求答复接收器
     * 
     * @author noear
     * @since 2.0
     * @param {java.util.concurrent.CompletableFuture} future
     * @param {number} timeout
     * @class
     */
    export class AcceptorRequest implements org.noear.socketd.transport.core.Acceptor {
        /*private*/ future: java.util.concurrent.CompletableFuture<org.noear.socketd.transport.core.Entity>;

        /*private*/ __timeout: number;

        public constructor(future: java.util.concurrent.CompletableFuture<org.noear.socketd.transport.core.Entity>, timeout: number) {
            if (this.future === undefined) { this.future = null; }
            if (this.__timeout === undefined) { this.__timeout = 0; }
            this.future = future;
            this.__timeout = timeout;
        }

        /**
         * 是否单发接收
         * 
         * @return {boolean}
         */
        public isSingle(): boolean {
            return true;
        }

        /**
         * 是否结束接收
         * 
         * @return {boolean}
         */
        public isDone(): boolean {
            return this.future.isDone();
        }

        /**
         * 超时设定（单位：毫秒）
         * 
         * @return {number}
         */
        public timeout(): number {
            return this.__timeout;
        }

        /**
         * 接收答复
         * 
         * @param {*} message
         * @param {*} onError
         */
        public accept(message: org.noear.socketd.transport.core.Message, onError: (p1: Error) => void) {
            this.future.complete(message);
        }
    }
    AcceptorRequest["__class"] = "org.noear.socketd.transport.core.internal.AcceptorRequest";
    AcceptorRequest["__interfaces"] = ["org.noear.socketd.transport.core.Acceptor"];


}

