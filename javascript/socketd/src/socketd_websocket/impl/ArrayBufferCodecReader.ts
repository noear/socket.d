import type {CodecReader} from "../../socketd/transport/core/Codec";

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

        let tmp = this._bufView.getInt8(this._bufViewIdx);
        this._bufViewIdx += 1;
        return tmp;
    }

    getBytes(dst: ArrayBuffer, offset: number, length: number) {
        let tmp = new DataView(dst);
        for (let i = 0; i < length; i++) {
            if (this._bufViewIdx >= this._buf.byteLength) {
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

        let tmp = this._bufView.getInt32(this._bufViewIdx);
        this._bufViewIdx += 4;
        return tmp;
    }

    remaining(): number {
        return this._buf.byteLength - this._bufViewIdx;
    }

    position(): number {
        return this._bufViewIdx;
    }
}