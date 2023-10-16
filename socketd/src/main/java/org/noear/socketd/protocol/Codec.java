package org.noear.socketd.protocol;

import java.io.IOException;

/**
 * @author noear 2023/10/14 created
 */
public interface Codec<T> {
    Frame decode(T buffer) throws IOException;
    T encode(Frame frame) throws IOException;
}
