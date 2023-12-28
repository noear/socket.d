package org.noear.socketd.transport.core;

/**
 * 消息内部接口
 *
 * @author noear
 * @since 2.0
 */
public interface MessageInternal extends Message, Reply {
    /**
     * 获取标记
     */
    int flag();
}