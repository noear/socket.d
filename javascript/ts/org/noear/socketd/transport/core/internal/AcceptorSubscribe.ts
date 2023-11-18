/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 订阅答复接收器
     * 
     * @author noear
     * @since 2.0
     * @param {*} future
     * @class
     */
    export class AcceptorSubscribe implements org.noear.socketd.transport.core.Acceptor {
        /*private*/ future: org.noear.socketd.utils.IoConsumer<org.noear.socketd.transport.core.Entity>;

        public constructor(future: org.noear.socketd.utils.IoConsumer<org.noear.socketd.transport.core.Entity>) {
            if (this.future === undefined) { this.future = null; }
            this.future = <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (t) =>  (funcInst['accept'] ? funcInst['accept'] : funcInst) .call(funcInst, t)})(future));
        }

        /**
         * 是否单发接收
         * @return {boolean}
         */
        public isSingle(): boolean {
            return false;
        }

        /**
         * 是否结束接收
         * @return {boolean}
         */
        public isDone(): boolean {
            return false;
        }

        /**
         * 超时设定（单位：毫秒）
         * @return {number}
         */
        public timeout(): number {
            return 0;
        }

        /**
         * 接收答复
         * @param {*} message
         * @param {*} onError
         */
        public accept(message: org.noear.socketd.transport.core.Message, onError: (p1: Error) => void) {
            try {
                this.future(message);
            } catch(e) {
                (target => (typeof target === 'function') ? target(e) : (<any>target).accept(e))(onError);
            }
        }
    }
    AcceptorSubscribe["__class"] = "org.noear.socketd.transport.core.internal.AcceptorSubscribe";
    AcceptorSubscribe["__interfaces"] = ["org.noear.socketd.transport.core.Acceptor"];


}

