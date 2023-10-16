package org.noear.socketd.server;

import org.noear.socketd.protocol.Processor;

import java.io.IOException;

/**
 * 服务端
 *
 * @author noear
 * @since 2.0
 */
public interface Server {
    void binding(Processor processor);

    void start() throws IOException;

    void stop() throws IOException;
}
