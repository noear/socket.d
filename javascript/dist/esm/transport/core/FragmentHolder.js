export class FragmentHolder {
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
