/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.buffer {
    /**
     * 缓冲读 ByteBuffer 适配
     * 
     * @author noear
     * @since 2.0
     * @param {java.nio.ByteBuffer} buffer
     * @class
     */
    export class ByteBufferReader implements org.noear.socketd.transport.core.buffer.BufferReader {
        /*private*/ buffer: java.nio.ByteBuffer;

        public constructor(buffer: java.nio.ByteBuffer) {
            if (this.buffer === undefined) { this.buffer = null; }
            this.buffer = buffer;
        }

        public get$(): number {
            return this.buffer.get();
        }

        public get$byte_A$int$int(dst: number[], offset: number, length: number) {
            this.buffer.get(dst, offset, length);
        }

        /**
         * 获取一组 byte
         * @param {byte[]} dst
         * @param {number} offset
         * @param {number} length
         */
        public get(dst?: any, offset?: any, length?: any) {
            if (((dst != null && dst instanceof <any>Array && (dst.length == 0 || dst[0] == null ||(typeof dst[0] === 'number'))) || dst === null) && ((typeof offset === 'number') || offset === null) && ((typeof length === 'number') || length === null)) {
                return <any>this.get$byte_A$int$int(dst, offset, length);
            } else if (dst === undefined && offset === undefined && length === undefined) {
                return <any>this.get$();
            } else throw new Error('invalid overload');
        }

        /**
         * 获取 int
         * @return {number}
         */
        public getInt(): number {
            return this.buffer.getInt();
        }

        /**
         * 剩余长度
         * @return {number}
         */
        public remaining(): number {
            return this.buffer.remaining();
        }

        /**
         * 当前位置
         * @return {number}
         */
        public position(): number {
            return this.buffer.position();
        }
    }
    ByteBufferReader["__class"] = "org.noear.socketd.transport.core.buffer.ByteBufferReader";
    ByteBufferReader["__interfaces"] = ["org.noear.socketd.transport.core.buffer.BufferReader"];


}

