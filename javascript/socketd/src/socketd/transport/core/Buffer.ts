export interface BufferReader {

    /**
     * 获取 byte
     */
    get(): number;

    /**
     * 获取一组 byte
     */
    get(dst: number[], offset: number, length: number);

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


export interface BufferWriter {
    /**
     * 推入一组 byte
     */
    putBytes(bytes: number[]);

    /**
     * 推入一组 byte
     */
    putBytes(src: number[], offset: number, length: number);

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