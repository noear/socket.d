import {BufferWriter} from "../../socketd/transport/core/Buffer";

export class BufferWriterImpl implements BufferWriter {
    _buf: ArrayBuffer;
    _bufView: DataView;
    _bufViewIdx: number;

    constructor(n: number) {
        this._buf = new ArrayBuffer(n);
        this._bufView = new DataView(this._buf);
        this._bufViewIdx = 0;
    }

    putBytes(src: ArrayBuffer) {
        let tmp = new DataView(src);
        let len = tmp.byteLength;

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