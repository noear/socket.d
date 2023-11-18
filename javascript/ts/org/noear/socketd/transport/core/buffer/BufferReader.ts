/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.buffer {
    /**
     * 缓冲读
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface BufferReader {
        /**
         * 获取一组 byte
         * @param {byte[]} dst
         * @param {number} offset
         * @param {number} length
         */
        get(dst?: any, offset?: any, length?: any);

        /**
         * 获取 int
         * @return {number}
         */
        getInt(): number;

        /**
         * 剩余长度
         * @return {number}
         */
        remaining(): number;

        /**
         * 当前位置
         * @return {number}
         */
        position(): number;
    }
}

