/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 数据分片处理
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface FragmentHandler {
        /**
         * 获取下个分片
         * @param {*} config
         * @param {java.util.concurrent.atomic.AtomicReference} fragmentIndex
         * @param {*} entity
         * @return {*}
         */
        nextFragment(config: org.noear.socketd.transport.core.Config, fragmentIndex: java.util.concurrent.atomic.AtomicReference<number>, entity: org.noear.socketd.transport.core.Entity): org.noear.socketd.transport.core.Entity;

        /**
         * 聚合所有分片
         * @param {*} channel
         * @param {number} fragmentIndex
         * @param {org.noear.socketd.transport.core.Frame} frame
         * @return {org.noear.socketd.transport.core.Frame}
         */
        aggrFragment(channel: org.noear.socketd.transport.core.Channel, fragmentIndex: number, frame: org.noear.socketd.transport.core.Frame): org.noear.socketd.transport.core.Frame;
    }
}

