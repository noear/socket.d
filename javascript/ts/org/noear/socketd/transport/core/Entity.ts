/**
 * 消息实体（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface Entity {
    /**
     * 获取元信息字符串（queryString style）
     * @return {string}
     */
    metaString(): string;

    /**
     * 获取元信息字典
     * @return {*}
     */
    metaMap(): any;

    /**
     * 获取元信息
     * @param {string} name
     * @return {string}
     */
    meta(name: string): string;

    /**
     * 获取元信息或默认
     * @param {string} name
     * @param {string} def
     * @return {string}
     */
    metaOrDefault(name: string, def: string): string;

    /**
     * 获取数据
     * @return {{ str: string, cursor: number }}
     */
    data(): { str: string, cursor: number };

    /**
     * 获取数据并转为字符串
     * @return {string}
     */
    dataAsString(): string;

    /**
     * 获取数据并转为字节数组
     * @return {byte[]}
     */
    dataAsBytes(): number[];

    /**
     * 获取数据长度
     * @return {number}
     */
    dataSize(): number;
}