/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.buffer {
    /**
     * 缓冲写 ByteBuffer 适配
     * 
     * @author noear
     * @since 2.0
     * @param {java.nio.ByteBuffer} target
     * @class
     */
    export class ByteBufferWriter implements org.noear.socketd.transport.core.buffer.BufferWriter {
        /*private*/ target: java.nio.ByteBuffer;

        public constructor(target: java.nio.ByteBuffer) {
            if (this.target === undefined) { this.target = null; }
            this.target = target;
        }

        public putBytes$byte_A(src: number[]) {
            this.target.put(src);
        }

        public putBytes$byte_A$int$int(src: number[], offset: number, length: number) {
            this.target.put(src, offset, length);
        }

        /**
         * 推入一组 byte
         * @param {byte[]} src
         * @param {number} offset
         * @param {number} length
         */
        public putBytes(src?: any, offset?: any, length?: any) {
            if (((src != null && src instanceof <any>Array && (src.length == 0 || src[0] == null ||(typeof src[0] === 'number'))) || src === null) && ((typeof offset === 'number') || offset === null) && ((typeof length === 'number') || length === null)) {
                return <any>this.putBytes$byte_A$int$int(src, offset, length);
            } else if (((src != null && src instanceof <any>Array && (src.length == 0 || src[0] == null ||(typeof src[0] === 'number'))) || src === null) && offset === undefined && length === undefined) {
                return <any>this.putBytes$byte_A(src);
            } else throw new Error('invalid overload');
        }

        /**
         * 推入 int
         * @param {number} val
         */
        public putInt(val: number) {
            this.target.putInt(val);
        }

        /**
         * 推入 char
         * @param {number} val
         */
        public putChar(val: number) {
            this.target.putChar(String.fromCharCode(val));
        }

        /**
         * 
         */
        public flush() {
            this.target.flip();
        }

        /**
         * 冲刷
         * @return {java.nio.ByteBuffer}
         */
        public getBuffer(): java.nio.ByteBuffer {
            return this.target;
        }
    }
    ByteBufferWriter["__class"] = "org.noear.socketd.transport.core.buffer.ByteBufferWriter";
    ByteBufferWriter["__interfaces"] = ["org.noear.socketd.transport.core.buffer.BufferWriter"];


}

