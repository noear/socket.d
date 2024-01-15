
import type {Frame} from "./Frame";
import type {IoFunction} from "./Typealias";



/**
 * 编解码缓冲读
 *
 * @author noear
 * @since 2.0
 */
export interface CodecReader {

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

    /*
     * 预看 byte
     * */
    peekByte(): number;

    /**
     * 跳过
     */
    skipBytes(length: number);

    /**
     * 剩余长度
     */
    remaining(): number;

    /**
     * 当前位置
     */
    position(): number;

    /**
     * 长度
     * */
    size(): number;

    /**
     * 重置索引
     * */
    reset();
}

/**
 * 编解码缓冲写
 *
 * @author noear
 * @since 2.0
 */
export interface CodecWriter {
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

/**
 * 编解码器
 *
 * @author noear
 * @since 2.0
 */
export interface Codec {
    /**
     * 编码读取
     *
     * @param buffer 缓冲
     */
    read(buffer: CodecReader): Frame | null;

    /**
     * 解码写入
     *
     * @param frame         帧
     * @param targetFactory 目标工厂
     */
    write<T extends CodecWriter>(frame: Frame, targetFactory: IoFunction<number, T>): T;
}

export class ArrayBufferCodecReader implements CodecReader {
    _buf: ArrayBuffer;
    _bufView: DataView;
    _bufViewIdx: number;

    constructor(buf: ArrayBuffer) {
        this._buf = buf;
        this._bufView = new DataView(buf);
        this._bufViewIdx = 0;
    }

    getByte(): number {
        if (this._bufViewIdx >= this._buf.byteLength) {
            return -1;
        }

        const tmp = this._bufView.getInt8(this._bufViewIdx);
        this._bufViewIdx += 1;
        return tmp;
    }

    getBytes(dst: ArrayBuffer, offset: number, length: number) {
        const tmp = new DataView(dst);
        const tmpEndIdx = offset + length;
        for (let i = offset; i < tmpEndIdx; i++) {
            if (this._bufViewIdx >= this._buf.byteLength) {
                //读完了
                break;
            }

            tmp.setInt8(i, this._bufView.getInt8(this._bufViewIdx));
            this._bufViewIdx++;
        }
    }

    getInt(): number {
        if (this._bufViewIdx >= this._buf.byteLength) {
            return -1;
        }

        const tmp = this._bufView.getInt32(this._bufViewIdx);
        this._bufViewIdx += 4;
        return tmp;
    }

    peekByte(): number {
        if (this.remaining() > 0) {
            return this._bufView.getInt8(this._bufViewIdx)
        } else {
            return -1;
        }
    }

    skipBytes(length: number) {
        this._bufViewIdx =  this.position() + length
    }

    remaining(): number {
        return this._buf.byteLength - this._bufViewIdx;
    }

    position(): number {
        return this._bufViewIdx;
    }


    size(): number {
        return this._buf.byteLength;
    }

    reset() {
        this._bufViewIdx = 0;
    }
}


export class ArrayBufferCodecWriter implements CodecWriter {
    _buf: ArrayBuffer;
    _bufView: DataView;
    _bufViewIdx: number;

    constructor(n: number) {
        this._buf = new ArrayBuffer(n);
        this._bufView = new DataView(this._buf);
        this._bufViewIdx = 0;
    }

    putBytes(src: ArrayBuffer) {
        const tmp = new DataView(src);
        const len = tmp.byteLength;

        for (let i = 0; i < len; i++) {
            this._bufView.setInt8(this._bufViewIdx, tmp.getInt8(i));
            this._bufViewIdx += 1;
        }
    }

    putInt(val: number) {
        this._bufView.setInt32(this._bufViewIdx, val);
        this._bufViewIdx += 4;
    }

    putChar(val: number) {
        this._bufView.setInt16(this._bufViewIdx, val);
        this._bufViewIdx += 2;
    }

    flush() {

    }

    getBuffer(): ArrayBuffer {
        return this._buf;
    }
}
