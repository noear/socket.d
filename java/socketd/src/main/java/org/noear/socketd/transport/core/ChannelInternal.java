package org.noear.socketd.transport.core;

/**
 * 通道内部扩展
 *
 * @author noear
 * @since 2.0
 */
public interface ChannelInternal extends Channel {
    /**
     * 设置会话
     * */
    void setSession(Session session);
}
