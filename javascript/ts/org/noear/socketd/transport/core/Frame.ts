import {MessageInternal} from "./MessageInternal";

/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 * @param {org.noear.socketd.transport.core.Flag} flag
 * @param {*} message
 * @class
 */
export class Frame {
    /*private*/
    flag: number;

    /*private*/
    message: MessageInternal;

    public constructor(flag: number, message: MessageInternal) {
        this.flag = flag;
        this.message = message;
    }

    /**
     * 标志
     *
     * @return number
     */
    public getFlag(): number {
        return this.flag;
    }

    /**
     * 消息
     *
     * @return {*}
     */
    public getMessage(): MessageInternal {
        return this.message;
    }

    /**
     *
     * @return {string}
     */
    public toString(): string {
        return "Frame{flag=" + this.flag + ", message=" + this.message + '}';
    }
}