package org.noear.socketd.broker.aio;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Processor;
import org.noear.socketd.protocol.impl.ProcessorDefault;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.transport.AioQuickServer;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class AioServer implements Server {
    private static final Logger log = LoggerFactory.getLogger(AioServer.class);

    private AioQuickServer server;
    private ServerConfig serverConfig;
    private Processor processor;

    public AioServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void listen(Listener listener) {
        processor = new ProcessorDefault(listener);
    }

    @Override
    public void start() throws IOException {
        if (serverConfig.getHost() != null) {
            server = new AioQuickServer(serverConfig.getPort(),
                    new AioProtocol(), new AioMessageProcessor());
        } else {
            server = new AioQuickServer(serverConfig.getHost(), serverConfig.getPort(),
                    new AioProtocol(), new AioMessageProcessor());
        }

        server.setThreadNum(serverConfig.getCoreThreads());
        server.setBannerEnabled(false);
        if (serverConfig.getReadBufferSize() > 0) {
            server.setReadBufferSize(serverConfig.getReadBufferSize());
        }
        if (serverConfig.getWriteBufferSize() > 0) {
            server.setWriteBuffer(serverConfig.getWriteBufferSize(), 16);
        }
        server.start();
    }

    @Override
    public void stop() throws IOException {
        server.shutdown();
    }
}
