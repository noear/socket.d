package org.noear.socketd.protocol;

import java.io.IOException;

/**
 * 输出目标
 *
 * @author noear
 * @since 2.0
 */
public interface OutputTarget<T> {
    /**
     * 写
     *
     * @param source 源
     * @param frame  帧
     */
    void write(T source, Frame frame) throws IOException;
}
