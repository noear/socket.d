package org.noear.socketd.core;

import java.io.IOException;

/**
 * 编解码器
 *
 * @author noear
 * @since 2.0
 */
public interface Codec<T> {
    /**
     * 编码
     */
    Frame decode(T buffer);

    /**
     * 解码
     */
    T encode(Frame frame);
}
