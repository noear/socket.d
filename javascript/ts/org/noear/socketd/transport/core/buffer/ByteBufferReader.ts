import {BufferReader} from "./BufferReader";

/**
 * 缓冲读 ByteBuffer 适配
 *
 * @author noear
 * @since 2.0
 * @param {java.nio.ByteBuffer} buffer
 * @class
 */
export class ByteBufferReader implements BufferReader {
    /*private*/ buffer: ArrayBuffer

    public constructor(buffer: ArrayBuffer) {
        this.buffer = buffer;
    }

    public get(): number {
        return this.buffer
    }



    /**
     * 获取一组 byte
     * @param {byte[]} dst
     * @param {number} offset
     * @param {number} length
     */
    public getBytes(dst: number[], offset: number, length: number) {
        this.buffer.get(dst, offset, length);
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