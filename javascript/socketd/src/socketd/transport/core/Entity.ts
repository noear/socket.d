import {StrUtils} from "../../utils/StrUtils";


/**
 * 消息实体（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
export interface Entity {
    /**
     * at
     *
     * @since 2.1
     */
    at();

    /**
     * 获取元信息字符串（queryString style）
     */
    metaString(): string;

    /**
     * 获取元信息字典
     */
    metaMap(): URLSearchParams;

    /**
     * 获取元信息
     */
    meta(name: string): string;

    /**
     * 获取元信息或默认
     */
    metaOrDefault(name: string, def: string): string;

    /**
     * 添加元信息
     * */
    putMeta(name: string, val: string);

    /**
     * 获取数据
     */
    data(): ArrayBuffer;

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

/**
 * 实体默认实现
 *
 * @author noear
 * @since 2.0
 */
export class EntityDefault implements Entity {
    private _metaMap: URLSearchParams
    private _data: ArrayBuffer;

    constructor() {
        this._metaMap = null;
        this._data = new ArrayBuffer(0);
    }

    /**
     * At
     * */
    at() {
        return this.meta("@");
    }

    /**
     * 设置元信息字符串
     * */
    metaStringSet(metaString: string): EntityDefault {
        this._metaMap = new URLSearchParams(metaString);
        return this;
    }

    /**
     * 放置元信息字典
     *
     * @param map 元信息字典
     */
    metaMapPut(map): EntityDefault {
        for (let name of map.prototype) {
            this.metaMap().set(name, map[name]);
        }
        return this;
    }

    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    metaPut(name: string, val: string): EntityDefault {
        this.metaMap().set(name, val);
        return this;
    }

    /**
     * 获取元信息字符串（queryString style）
     */
    metaString(): string {
        return this.metaMap().toString();
    }

    /**
     * 获取元信息字典
     */
    metaMap(): URLSearchParams {
        if (this._metaMap == null) {
            this._metaMap = new URLSearchParams();
        }

        return this._metaMap;
    }

    /**
     * 获取元信息
     *
     * @param name 名字
     */
    meta(name: string): string {
        return this.metaMap().get(name);
    }

    /**
     * 获取元信息或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    metaOrDefault(name: string, def: string): string {
        let val = this.meta(name);
        if (val == null) {
            return val;
        } else {
            return def;
        }
    }

    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    putMeta(name: string, val: string) {
        this.metaPut(name, val);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    dataSet(data: ArrayBuffer): EntityDefault {
        this._data = data;
        return this;
    }

    /**
     * 获取数据（若多次复用，需要reset）
     */
    data(): ArrayBuffer {
        return this._data;
    }

    /**
     * 获取数据并转成字符串
     */
    dataAsString(): string {
        throw new Error("Method not implemented.");
    }

    /**
     * 获取数据长度
     */
    dataSize(): number {
        return this._data.byteLength;
    }

    /**
     * 释放资源
     */
    release() {

    }
}

/**
 * 字符串实体
 *
 * @author noear
 * @since 2.0
 */
export class StringEntity extends EntityDefault implements Entity{
    constructor(data: string) {
        super();
        const dataBuf = StrUtils.strToBuf(data);
        this.dataSet(dataBuf);
    }
}
