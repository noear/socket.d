import {Channel} from "./Channel";
import {Constants} from "./Constants";

/**
 * 断言
 *
 * @author noear
 * @since 2.0
 */
export class Asserts {
    /**
     * 断言关闭
     */
    static assertClosed(channel: Channel) {
        if (channel != null && channel.isClosed() > 0) {
            throw new Error("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

    /**
     * 断言关闭
     */
    static assertClosedByUser(channel: Channel) {
        if (channel != null && channel.isClosed() == Constants.CLOSE4_USER) {
            throw new Error("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

    /**
     * 断言 null
     */
    static assertNull(name: string, val: object) {
        if (val == null) {
            throw new Error("The argument cannot be null: " + name);
        }
    }

    /**
     * 断言 empty
     */
    static assertEmpty(name: string, val: string) {
        if (!val) {
            throw new Error("The argument cannot be empty: " + name);
        }
    }

    /**
     * 断言 size
     */
    static assertSize(name: string, size: number, limitSize: number) {
        if (size > limitSize) {
            let message = `This message ${name} size is out of limit ${limitSize} (${size})`;

            throw new Error(message);
        }
    }
}