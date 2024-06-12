import {Flags} from "./Flags";
import {MessageInternal} from "./Message";

/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
export class Frame {
    private _flag: number;
    private _message: MessageInternal | null;

    constructor(flag: number, message: MessageInternal | null) {
        this._flag = flag;
        this._message = message;
    }

    /**
     * 标志（保持与 Message 的获取风格）
     * */
    flag(): number {
        return this._flag;
    }

    /**
     * 消息
     * */
    message(): MessageInternal | null {
        return this._message;
    }

    toString(): string {
        return "Frame{" +
            "flag=" + Flags.name(this._flag) +
            ", message=" + this._message +
            '}';
    }
}