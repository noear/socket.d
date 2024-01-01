"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.Asserts = void 0;
const Constants_1 = require("./Constants");
const SocketdException_1 = require("../../exception/SocketdException");
/**
 * 断言
 *
 * @author noear
 * @since 2.0
 */
class Asserts {
    /**
     * 断言关闭
     */
    static assertClosed(channel) {
        if (channel != null && channel.isClosed() > 0) {
            throw new SocketdException_1.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }
    /**
     * 断言关闭
     */
    static assertClosedByUser(channel) {
        if (channel != null && channel.isClosed() == Constants_1.Constants.CLOSE4_USER) {
            throw new SocketdException_1.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
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
            throw new SocketdException_1.SocketdSizeLimitException(message);
        }
    }
}
exports.Asserts = Asserts;
