export class ArrayBufferCodecReader {
    constructor(buf) {
        this._buf = buf;
        this._bufView = new DataView(buf);
        this._bufViewIdx = 0;
    }
    getByte() {
        if (this._bufViewIdx >= this._buf.byteLength) {
            return -1;
        }
        const tmp = this._bufView.getInt8(this._bufViewIdx);
        this._bufViewIdx += 1;
        return tmp;
    }
    getBytes(dst, offset, length) {
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
    getInt() {
        if (this._bufViewIdx >= this._buf.byteLength) {
            return -1;
        }
        const tmp = this._bufView.getInt32(this._bufViewIdx);
        this._bufViewIdx += 4;
        return tmp;
    }
    remaining() {
        return this._buf.byteLength - this._bufViewIdx;
    }
    position() {
        return this._bufViewIdx;
    }
    size() {
        return this._buf.byteLength;
    }
    reset() {
        this._bufViewIdx = 0;
    }
}
export class ArrayBufferCodecWriter {
    constructor(n) {
        this._buf = new ArrayBuffer(n);
        this._bufView = new DataView(this._buf);
        this._bufViewIdx = 0;
    }
    putBytes(src) {
        const tmp = new DataView(src);
        const len = tmp.byteLength;
        for (let i = 0; i < len; i++) {
            this._bufView.setInt8(this._bufViewIdx, tmp.getInt8(i));
            this._bufViewIdx += 1;
        }
    }
    putInt(val) {
        this._bufView.setInt32(this._bufViewIdx, val);
        this._bufViewIdx += 4;
    }
    putChar(val) {
        this._bufView.setInt16(this._bufViewIdx, val);
        this._bufViewIdx += 2;
    }
    flush() {
    }
    getBuffer() {
        return this._buf;
    }
}
