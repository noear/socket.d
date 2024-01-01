import { Constants } from "./Constants";
import { SocketdChannelException, SocketdSizeLimitException } from "../../exception/SocketdException";
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
    static assertClosed(channel) {
        if (channel != null && channel.isClosed() > 0) {
            throw new SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }
    /**
     * 断言关闭
     */
    static assertClosedByUser(channel) {
        if (channel != null && channel.isClosed() == Constants.CLOSE4_USER) {
            throw new SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }
    /**
     * 断言 null
     */
    static assertNull(name, val) {
        if (val == null) {
            throw new Error("The argument cannot be null: " + name);
        }
    }
    /**
     * 断言 empty
     */
    static assertEmpty(name, val) {
        if (!val) {
            throw new Error("The argument cannot be empty: " + name);
        }
    }
    /**
     * 断言 size
     */
    static assertSize(name, size, limitSize) {
        if (size > limitSize) {
            const message = `This message ${name} size is out of limit ${limitSize} (${size})`;
            throw new SocketdSizeLimitException(message);
        }
    }
}
