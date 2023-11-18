package org.noear.socketd.transport.core;

import org.noear.socketd.exception.SocketdChannelException;

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

    /**
     * 断言关闭
     */
    public static void assertClosedByUser(Channel channel) {
        if (channel != null && channel.isClosed() == Constants.CLOSE3_USER) {
            throw new SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }

    /**
     * 断言null
     * */
    public static void assertNull(Object val, String name) {
        if (val == null) {
            throw new IllegalArgumentException("The argument cannot be null: " + name);
        }
    }
}
