/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.buffer {
    /**
     * 缓冲写
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface BufferWriter {
        /**
         * 推入一组 byte
         * @param {byte[]} src
         * @param {number} offset
         * @param {number} length
         */
        putBytes(src?: any, offset?: any, length?: any);

        /**
         * 推入 int
         * @param {number} val
         */
        putInt(val: number);

        /**
         * 推入 char
         * @param {number} val
         */
        putChar(val: number);

        /**
         * 冲刷
         */
        flush();
    }
}

