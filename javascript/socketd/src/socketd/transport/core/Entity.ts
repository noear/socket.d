import {CodecReader} from "./Codec";
import {type Buffer} from "./Buffer";


/**
 * 消息实体（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
export interface Entity {
    /**
     * 获取元信息字符串（queryString style）
     */
    metaString(): string;

    /**
     * 获取元信息字典
     */
    metaMap(): Map<string,string>;

    /**
     * 获取元信息
     */
    meta(name: string): string | null;

    /**
     * 获取元信息或默认
     */
    metaOrDefault(name: string, def: string): string;

    /**
     * 获取元信息并转为 int
     */
    metaAsInt(name:string):number;

    /**
     * 获取元信息并转为 long(int)
     */
    metaAsLong(name:string):number;

    /**
     * 获取元信息并转为 float
     */
    metaAsFloat(name:string):number;

    /**
     * 获取元信息并转为 double(float)
     */
    metaAsDouble(name:string):number;

    /**
     * 添加元信息
     * */
    putMeta(name: string, val: string | null);

    /**
     * 删除元信息
     * */
    delMeta(name: string);

    /**
     * 获取数据
     */
    data(): Buffer;

    /**
     * 获取数据并转为读取器
     */
    dataAsReader(): CodecReader;

    /**
     * 获取数据并转为字符串
     */
    dataAsString(): string;

    /**
     * 获取数据长度
     */
    dataSize(): number;

    /**
     * 释放资源
     */
    release();
}

/**
 * 答复实体
 *
 * @author noear
 * @since 2.1
 */
export interface Reply extends Entity {
    /**
     * 流Id
     */
    sid(): string;

    /**
     * 是否答复结束
     */
    isEnd(): boolean
}
