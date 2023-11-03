package org.noear.socketd.transport.core;

import org.noear.socketd.exception.SocketdChannelException;

/**
 * @author noear
 * @since 2.0
 */
public class Asserts {
    public static void assertClosed(Channel channel){
        if(channel.isClosed()) {
            throw new SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().getSessionId());
        }
    }
}
