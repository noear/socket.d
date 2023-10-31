package org.noear.socketd.protocol;

import java.io.IOException;
import java.net.InetAddress;

/**
 * 通道助理
 *
 * @author noear
 * @since 2.0
 */
public interface ChannelAssistant<T> {
    /**
     * 写入
     *
     * @param target 目标
     * @param frame  帧
     */
    void write(T target, Frame frame) throws IOException;

    /**
     * 是否有效
     */
    boolean isValid(T target);

    /**
     * 关闭
     */
    void close(T target) throws IOException;

    /**
     * 获取远程地址
     */
    InetAddress getRemoteAddress(T target) throws IOException;

    /**
     * 获取本地地址
     */
    InetAddress getLocalAddress(T target) throws IOException;
}