/**
 * 缓冲读
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface BufferReader {

    /**
     * 获取 byte
     */
    get(): number

    /**
     * 获取一组 byte
     */
    getBytes(dst: ArrayBuffer, offset: number, length: number);

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