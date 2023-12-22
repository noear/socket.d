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
     * 编码
     */
    Frame read(BufferReader buffer);

    /**
     * 解码
     */
    <T extends BufferWriter> T write(Frame frame, Function<Integer, T> targetFactory) throws IOException;
}
