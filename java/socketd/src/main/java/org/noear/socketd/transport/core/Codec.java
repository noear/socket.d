package org.noear.socketd.transport.core;

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
     * @param reader 读取器
     */
    Frame read(CodecReader reader);

    /**
     * 解码写入
     *
     * @param frame         帧
     * @param writerFactory 书写器工厂
     */
    <T extends CodecWriter> T write(Frame frame, Function<Integer, T> writerFactory) throws IOException;
}
