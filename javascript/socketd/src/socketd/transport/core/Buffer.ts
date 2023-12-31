import type {IoConsumer} from "./Typealias";

/**
 * 缓冲（适配 ArrayBuffer 与 Blob 两种不同接口）
 * */
export interface Buffer {
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

    /**
     * 获取数据
     * */
    getBytes(length: number, callback: IoConsumer<ArrayBuffer>) : boolean;

    /**
     * 获取 blob?
     * */
    getBlob(): Blob | null;

    /**
     * 获取 ArrayBuffer?
     * */
    getArray(): ArrayBuffer | null;
}

export class ByteBuffer implements Buffer {
    private _buf: ArrayBuffer;
    private _bufView?: DataView;
    private _bufViewIdx = 0;

    constructor(buf: ArrayBuffer) {
        this._buf = buf;
    }

    remaining(): number {
        return this.size() - this.position();
    }
    position(): number {
        return  this._bufViewIdx;
    }
    size(): number {
        return this._buf.byteLength;
    }
    reset() {
        this._bufViewIdx = 0;
    }
    getBytes(length: number, callback: IoConsumer<ArrayBuffer>) : boolean {
        if (!this._bufView) {
            this._bufView = new DataView(this._buf);
        }

        let tmpSize = this.remaining();
        if (tmpSize > length) {
            tmpSize = length;
        }

        if (tmpSize <= 0) {
            return false;
        }

        const tmp = new ArrayBuffer(tmpSize);
        const tmpView = new DataView(tmp);

        for (let i = 0; i < tmpSize; i++) {
            tmpView.setInt8(i, this._bufView.getInt8(this._bufViewIdx));
            this._bufViewIdx++;
        }

        callback(tmp);
        return true;
    }

    getBlob(): Blob | null {
        return null;
    }

    getArray(): ArrayBuffer | null {
        return this._buf;
    }
}

export class BlobBuffer implements Buffer {
    private _buf: Blob;
    private _bufIdx = 0;

    constructor(buf: Blob) {
        this._buf = buf;
    }

    remaining(): number {
        return this._buf.size - this._bufIdx;
    }
    position(): number {
        return this._bufIdx;
    }
    size(): number {
        return this._buf.size;
    }
    reset() {
        this._bufIdx = 0;
    }
    getBytes(length: number, callback: IoConsumer<ArrayBuffer>) : boolean {
        let tmpSize = this.remaining();
        if (tmpSize > length) {
            tmpSize = length;
        }

        if (tmpSize <= 0) {
            return false;
        }

        let tmp = this._buf.slice(this._bufIdx, tmpSize);
        let tmpReader = new FileReader();
        tmpReader.onload = (event) => {
            if (event.target) {
                //成功读取
                this._bufIdx = this._bufIdx + tmpSize;
                callback(event.target!.result as ArrayBuffer);
            }
        };
        tmpReader.readAsArrayBuffer(tmp);

        return true;
    }

    getBlob(): Blob | null {
        return this._buf;
    }

    getArray(): ArrayBuffer | null {
        return null;
    }
}