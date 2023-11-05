package org.noear.socketd.transport.core;

import java.io.IOException;
import java.util.function.Function;

/**
 * 编解码器
 *
 * @author noear
 * @since 2.0
 */
public interface Codec<In,Out> {
    /**
     * 编码
     */
    Frame read(In buffer);

    /**
     * 解码
     */
    <T extends Out> T write(Frame frame, Function<Integer, T> target) throws IOException;
}
