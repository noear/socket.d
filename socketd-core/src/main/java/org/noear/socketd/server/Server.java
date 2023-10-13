package org.noear.socketd.server;

import org.noear.socketd.Listener;

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
