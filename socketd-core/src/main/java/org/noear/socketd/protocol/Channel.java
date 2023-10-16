package org.noear.socketd.protocol;

import org.noear.socketd.Session;

import java.io.IOException;

/**
 * 通道
 *
 * @author noear
 * @since 2.0
 */
public interface Channel<S> {
    void sendConnect(String uri) throws IOException;
    void sendConnack() throws IOException;

    void sendPing() throws IOException;

    void sendPong() throws IOException;

    void send(Frame frame) throws IOException;

    /**
     * 接收帧
     */
    Frame receive() throws IOException;


    /**
     * 获取握手信息
     */
    Handshaker getHandshaker();

    /**
     * 获取会话
     */
    Session getSession();
}
