
/**
 * 缓冲读
 *
 * @author noear
 * @since 2.0
 */
export interface BufferReader {

    /**
     * 获取 byte
     */
    getByte(): number;

    /**
     * 获取一组 byte
     */
    getBytes(dst: ArrayBuffer, offset: number, length: number);

    /**
     * 获取 int
     */
    getInt(): number;

    /**
     * 剩余长度
     */
    remaining(): number;

    /**
     * 当前位置
     */
    position(): number;
}

/**
 * 缓冲写
 *
 * @author noear
 * @since 2.0
 */
export interface BufferWriter {
    /**
     * 推入一组 byte
     */
    putBytes(src: ArrayBuffer);

    /**
     * 推入 int
     */
    putInt(val: number);

    /**
     * 推入 char
     */
    putChar(val: number);

    /**
     * 冲刷
     */
    flush();
}