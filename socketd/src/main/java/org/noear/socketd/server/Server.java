package org.noear.socketd.server;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Processor;

import java.io.IOException;

/**
 * 服务端
 *
 * @author noear
 * @since 2.0
 */
public interface Server {
    void listen(Listener listener);

    void start() throws IOException;

    void stop() throws IOException;
}
