package org.noear.socketd.protocol;

import org.noear.socketd.Session;

/**
 * 通道
 *
 * @author noear
 * @since 2.0
 */
public interface Channel {
    void sendHandshaked();
    void sendPing();
    void sendPong();
    void send(Frame frame);
    Handshaker getHandshaker();
    Session getSession();
}
