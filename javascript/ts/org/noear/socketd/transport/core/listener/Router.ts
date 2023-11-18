/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.listener {
    /**
     * 路由器
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Router {
        /**
         * 匹配
         * @param {string} path
         * @return {*}
         */
        matching(path: string): org.noear.socketd.transport.core.Listener;

        /**
         * 添加
         * @param {string} path
         * @param {*} listener
         */
        add(path: string, listener: org.noear.socketd.transport.core.Listener);

        /**
         * 移除
         * @param {string} path
         */
        remove(path: string);

        /**
         * 数量
         * @return {number}
         */
        count(): number;
    }
}

