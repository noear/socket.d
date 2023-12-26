package org.noear.socketd.transport.core;

import org.noear.socketd.transport.core.buffer.BufferReader;
import org.noear.socketd.transport.core.buffer.BufferWriter;

import java.io.IOException;
import java.util.function.Function;

/**
 * 编解码器
 *
 * @author noear
 * @since 2.0
 */
public interface Codec {
    /**
     * 编码读取
     *
     * @param buffer 缓冲
     */
    Frame read(BufferReader buffer);

    /**
     * 解码写入
     *
     * @param frame         帧
     * @param targetFactory 目标工厂
     */
    <T extends BufferWriter> T write(Frame frame, Function<Integer, T> targetFactory) throws IOException;
}
