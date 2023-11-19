package org.noear.socketd.transport.core;

/**
 * @author noear
 * @since 2.0
 */
public interface MessageInternal extends Message {
    /**
     * 获取标记
     */
    int flag();
}