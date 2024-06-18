import {BlobBuffer, Buffer, ByteBuffer} from "../Buffer";
import {ArrayBufferCodecReader, CodecReader} from "../Codec";
import {Constants} from "../Constants";
import {EntityMetas} from "../EntityMetas";
import {SocketDException} from "../../../exception/SocketDException";
import {StrUtils} from "../../../utils/StrUtils";
import {Entity} from "../Entity";

/**
 * 实体默认实现
 *
 * @author noear
 * @since 2.0
 */
export class EntityDefault implements Entity {
    private _metaMap: Map<string, string> | null;
    private _data: Buffer;
    private _dataAsReader: CodecReader | null;

    constructor() {
        this._metaMap = null;
        this._data = Constants.DEF_DATA;
        this._dataAsReader = null;
    }

    /**
     * At player name
     */
    at(name: string): EntityDefault {
        this.metaPut("@", name);
        return this;
    }

    /**
     * Range
     * */
    range(start: number, size: number): EntityDefault {
        this.metaPut(EntityMetas.META_RANGE_START, start.toString());
        this.metaPut(EntityMetas.META_RANGE_SIZE, size.toString());
        return this;
    }

    /**
     * 设置元信息字符串
     * */
    metaStringSet(metaString: string): EntityDefault {
        this._metaMap = new Map<string, string>();

        //此处要优化
        if (metaString) {
            for (const kvStr of metaString.split("&")) {
                const idx = kvStr.indexOf('=');
                if (idx > 0) {
                    this._metaMap.set(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                }
            }
        }

        return this;
    }

    /**
     * 放置元信息字典
     *
     * @param map 元信息字典
     */
    metaMapPut(map: any): EntityDefault {
        if (map) {
            if (map instanceof Map) {
                map.forEach((val, key, p) => {
                    this.metaMap().set(key, val);
                })
            } else {
                for (const name of map.prototype) {
                    this.metaMap().set(name, map[name]);
                }
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
    metaPut(name: string, val: string | null): EntityDefault {
        if (val == null) {
            this.metaMap().delete(name);
        } else {
            this.metaMap().set(name, val);
        }

        return this;
    }

    /**
     * 删除元信息
     *
     * @param name 名字
     */
    metaDel(name:string){
        this.metaMap().delete(name);
    }

    /**
     * 获取元信息字符串（queryString style）
     */
    metaString(): string {
        let str = "";
        this.metaMap().forEach((val, key, p) => {
            str += `${key}=${val}&`;
        });
        if (str.length > 0) {
            return str.substring(0, str.length - 1);
        } else {
            return str;
        }
    }

    /**
     * 获取元信息字典
     */
    metaMap(): Map<string, string> {
        if (this._metaMap == null) {
            this._metaMap = new Map<string, string>();
        }

        return this._metaMap;
    }

    /**
     * 获取元信息
     *
     * @param name 名字
     */
    meta(name: string): string | null {
        let tmp = this.metaMap().get(name);
        return tmp ? tmp : null;
    }

    /**
     * 获取元信息或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    metaOrDefault(name: string, def: string): string {
        const val = this.meta(name);
        if (val) {
            return val;
        } else {
            return def;
        }
    }

    /**
     * 获取元信息并转为 int
     */
    metaAsInt(name: string): number {
        return parseInt(this.metaOrDefault(name, '0'));
    }

    /**
     * 获取元信息并转为 long(int)
     */
    metaAsLong(name: string): number {
        return this.metaAsInt(name);
    }

    /**
     * 获取元信息并转为 float
     */
    metaAsFloat(name: string): number {
        return parseFloat(this.metaOrDefault(name, '0'));
    }

    /**
     * 获取元信息并转为 double(float)
     */
    metaAsDouble(name: string): number {
        return this.metaAsFloat(name);
    }

    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    putMeta(name: string, val: string| null) {
        this.metaPut(name, val);
    }

    /**
     * 删除元信息
     *
     * @param name 名字
     */
    delMeta(name: string) {
        this.metaDel(name);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    dataSet(data: Blob | ArrayBuffer | Buffer): EntityDefault {
        if (data instanceof BlobBuffer || data instanceof ByteBuffer) {
            this._data = data;
        } else if (data instanceof ArrayBuffer) {
            this._data = new ByteBuffer(data);
        } else if (typeof (Blob) != 'undefined' && data instanceof Blob) {
            this._data = new BlobBuffer(data);
        } else {
            console.warn("This data type is not supported, type=" + typeof (data));
        }

        return this;
    }

    /**
     * 获取数据（若多次复用，需要reset）
     */
    data(): Buffer {
        return this._data;
    }

    dataAsReader(): CodecReader {
        if (this._data.getArray() == null) {
            throw new SocketDException("Blob does not support dataAsReader");
        }

        if (!this._dataAsReader) {
            this._dataAsReader = new ArrayBufferCodecReader(this._data.getArray()!);
        }

        return this._dataAsReader;
    }

    /**
     * 获取数据并转成字符串
     */
    dataAsString(): string {
        if (this._data.getArray() == null) {
            throw new SocketDException("Blob does not support dataAsString");
        }

        return StrUtils.bufToStrDo(this._data.getArray()!, '');
    }

    /**
     * 获取数据长度
     */
    dataSize(): number {
        return this._data.size();
    }

    /**
     * 释放资源
     */
    release() {

    }

    toString(): string {
        return "Entity{" +
            "meta='" + this.metaString() + '\'' +
            ", data=byte[" + this.dataSize() + ']' + //避免内容太大，影响打印
            '}';
    }
}