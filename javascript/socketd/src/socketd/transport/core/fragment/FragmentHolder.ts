import type {MessageInternal} from "../Message";

export class FragmentHolder {
    private _index: number;
    private _message: MessageInternal;

    constructor(index: number, message: MessageInternal) {
        this._index = index;
        this._message = message;
    }

    /**
     * 获取顺序位
     */
    getIndex(): number {
        return this._index;
    }

    /**
     * 获取分片帧
     */
    getMessage(): MessageInternal {
        return this._message;
    }
}