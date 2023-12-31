"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.BlobBuffer = exports.ByteBuffer = void 0;
class ByteBuffer {
    constructor(buf) {
        this._bufIdx = 0;
        this._buf = buf;
    }
    remaining() {
        return this.size() - this.position();
    }
    position() {
        return this._bufIdx;
    }
    size() {
        return this._buf.byteLength;
    }
    reset() {
        this._bufIdx = 0;
    }
    getBytes(length, callback) {
        let tmpSize = this.remaining();
        if (tmpSize > length) {
            tmpSize = length;
        }
        if (tmpSize <= 0) {
            return false;
        }
        let tmpEnd = this._bufIdx + tmpSize;
        let tmp = this._buf.slice(this._bufIdx, tmpEnd);
        this._bufIdx = tmpEnd;
        callback(tmp);
        return true;
    }
    getBlob() {
        return null;
    }
    getArray() {
        return this._buf;
    }
}
exports.ByteBuffer = ByteBuffer;
class BlobBuffer {
    constructor(buf) {
        this._bufIdx = 0;
        this._buf = buf;
    }
    remaining() {
        return this._buf.size - this._bufIdx;
    }
    position() {
        return this._bufIdx;
    }
    size() {
        return this._buf.size;
    }
    reset() {
        this._bufIdx = 0;
    }
    getBytes(length, callback) {
        let tmpSize = this.remaining();
        if (tmpSize > length) {
            tmpSize = length;
        }
        if (tmpSize <= 0) {
            return false;
        }
        let tmpEnd = this._bufIdx + tmpSize;
        let tmp = this._buf.slice(this._bufIdx, tmpEnd);
        let tmpReader = new FileReader();
        tmpReader.onload = (event) => {
            if (event.target) {
                //成功读取
                callback(event.target.result);
            }
        };
        tmpReader.readAsArrayBuffer(tmp);
        this._bufIdx = tmpEnd;
        return true;
    }
    getBlob() {
        return this._buf;
    }
    getArray() {
        return null;
    }
}
exports.BlobBuffer = BlobBuffer;
