import { StrUtils } from "../../utils/StrUtils";
import { ArrayBufferCodecReader } from "./Codec";
import { Constants, EntityMetas } from "./Constants";
import { BlobBuffer, ByteBuffer } from "./Buffer";
import { SocketdException } from "../../exception/SocketdException";
/**
 * 实体默认实现
 *
 * @author noear
 * @since 2.0
 */
export class EntityDefault {
    constructor() {
        this._metaMap = null;
        this._data = Constants.DEF_DATA;
        this._dataAsReader = null;
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
    metaStringSet(metaString) {
        this._metaMap = new URLSearchParams(metaString);
        return this;
    }
    /**
     * 放置元信息字典
     *
     * @param map 元信息字典
     */
    metaMapPut(map) {
        if (map instanceof URLSearchParams) {
            const tmp = map;
            tmp.forEach((val, key, p) => {
                this.metaMap().set(key, val);
            });
        }
        else {
            for (const name of map.prototype) {
                this.metaMap().set(name, map[name]);
            }
        }
        return this;
    }
    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    metaPut(name, val) {
        this.metaMap().set(name, val);
        return this;
    }
    /**
     * 获取元信息字符串（queryString style）
     */
    metaString() {
        return this.metaMap().toString();
    }
    /**
     * 获取元信息字典
     */
    metaMap() {
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
    meta(name) {
        return this.metaMap().get(name);
    }
    /**
     * 获取元信息或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    metaOrDefault(name, def) {
        const val = this.meta(name);
        if (val) {
            return val;
        }
        else {
            return def;
        }
    }
    /**
     * 获取元信息并转为 int
     */
    metaAsInt(name) {
        return parseInt(this.metaOrDefault(name, '0'));
    }
    /**
     * 获取元信息并转为 float
     */
    metaAsFloat(name) {
        return parseFloat(this.metaOrDefault(name, '0'));
    }
    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    putMeta(name, val) {
        this.metaPut(name, val);
    }
    /**
     * 设置数据
     *
     * @param data 数据
     */
    dataSet(data) {
        if (data instanceof Blob) {
            this._data = new BlobBuffer(data);
        }
        else {
            this._data = new ByteBuffer(data);
        }
        return this;
    }
    /**
     * 获取数据（若多次复用，需要reset）
     */
    data() {
        return this._data;
    }
    dataAsReader() {
        if (this._data.getArray() == null) {
            throw new SocketdException("Blob does not support dataAsReader");
        }
        if (!this._dataAsReader) {
            this._dataAsReader = new ArrayBufferCodecReader(this._data.getArray());
        }
        return this._dataAsReader;
    }
    /**
     * 获取数据并转成字符串
     */
    dataAsString() {
        if (this._data.getArray() == null) {
            throw new SocketdException("Blob does not support dataAsString");
        }
        return StrUtils.bufToStrDo(this._data.getArray(), '');
    }
    /**
     * 获取数据长度
     */
    dataSize() {
        return this._data.size();
    }
    /**
     * 释放资源
     */
    release() {
    }
    toString() {
        return "Entity{" +
            "meta='" + this.metaString() + '\'' +
            ", data=byte[" + this.dataSize() + ']' + //避免内容太大，影响打印
            '}';
    }
}
/**
 * 字符串实体
 *
 * @author noear
 * @since 2.0
 */
export class StringEntity extends EntityDefault {
    constructor(data) {
        super();
        const dataBuf = StrUtils.strToBuf(data);
        this.dataSet(dataBuf);
    }
}
export class FileEntity extends EntityDefault {
    constructor(file) {
        super();
        this.dataSet(file);
        this.metaPut(EntityMetas.META_DATA_DISPOSITION_FILENAME, file.name);
    }
}
