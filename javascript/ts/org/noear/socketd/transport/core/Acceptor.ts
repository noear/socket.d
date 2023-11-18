/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 答复接收器
     * 
     * @author noear
     * @since 1.0
     * @class
     */
    export interface Acceptor {
        /**
         * 是否单发接收
         * 
         * @return {boolean}
         */
        isSingle(): boolean;

        /**
         * 是否结束接收
         * 
         * @return {boolean}
         */
        isDone(): boolean;

        /**
         * 超时设定（单位：毫秒）
         * 
         * @return {number}
         */
        timeout(): number;

        /**
         * 接收答复
         * 
         * @param {*} message
         * @param {*} onError
         */
        accept(message: org.noear.socketd.transport.core.Message, onError: (p1: Error) => void);
    }
}

