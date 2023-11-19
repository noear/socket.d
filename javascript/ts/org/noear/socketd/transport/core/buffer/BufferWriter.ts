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
    putBytes(src: ArrayBuffer, offset: number, length: number);

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