"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.FragmentHolder = void 0;
class FragmentHolder {
    constructor(index, message) {
        this._index = index;
        this._message = message;
    }
    /**
     * 获取顺序位
     */
    getIndex() {
        return this._index;
    }
    /**
     * 获取分片帧
     */
    getMessage() {
        return this._message;
    }
}
exports.FragmentHolder = FragmentHolder;
