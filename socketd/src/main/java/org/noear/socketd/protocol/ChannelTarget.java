package org.noear.socketd.protocol;

import java.io.IOException;

/**
 * 输出目标
 *
 * @author noear
 * @since 2.0
 */
public interface ChannelTarget<T> {
    /**
     * 写
     *
     * @param source 源
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
}
