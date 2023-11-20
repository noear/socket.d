package org.noear.socketd.transport.core;

/**
 * @author noear
 * @since 2.0
 */
public interface HandshakeInternal extends Handshake {
    /**
     * 获取消息源
     */
    MessageInternal getSource();
}
