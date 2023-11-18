/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 编解码器
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface Codec<In, Out> {
        /**
         * 编码
         * @param {*} buffer
         * @return {org.noear.socketd.transport.core.Frame}
         */
        read(buffer: In): org.noear.socketd.transport.core.Frame;
    }
}

