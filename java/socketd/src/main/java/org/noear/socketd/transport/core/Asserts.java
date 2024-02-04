package org.noear.socketd.transport.core;

import org.noear.socketd.exception.SocketdChannelException;
import org.noear.socketd.exception.SocketdSizeLimitException;
import org.noear.socketd.utils.StrUtils;

/**
 * 断言
 *
 * @author noear
 * @since 2.0
 */
public class Asserts {
    /**
     * 断言关闭
     */
    public static void assertClosed(Channel channel) {
        if (channel != null && channel.isClosed() > 0) {
            throw new SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

    public static boolean isClosedAndEnd(Channel channel) {
        return channel.isClosed() == Constants.CLOSE29_USER
                || channel.isClosed() == Constants.CLOSE28_OPEN_FAIL
                || channel.isClosed() == Constants.CLOSE12_PROTOCOL_CLOSE_STARTING
                || channel.isClosed() == Constants.CLOSE19_PROTOCOL_USER;
    }

    /**
     * 断言关闭并束了
     */
    public static void assertClosedAndEnd(Channel channel) {
        if (channel != null && isClosedAndEnd(channel)) {
            throw new SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

    /**
     * 断言 null
     */
    public static void assertNull(String name, Object val) {
        if (val == null) {
            throw new IllegalArgumentException("The argument cannot be null: " + name);
        }
    }

    /**
     * 断言 empty
     */
    public static void assertEmpty(String name, String val) {
        if (StrUtils.isEmpty(val)) {
            throw new IllegalArgumentException("The argument cannot be empty: " + name);
        }
    }


    /**
     * 断言 size
     */
    public static void assertSize(String name, int size, int limitSize) {
        if (size > limitSize) {
            StringBuilder buf = new StringBuilder();
            buf.append("This message ").append(name).append(" size is out of limit ").append(limitSize)
                    .append(" (").append(size).append(")");
            throw new SocketdSizeLimitException(buf.toString());
        }
    }
}