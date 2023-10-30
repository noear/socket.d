package org.noear.socketd.broker.websocket;

import org.noear.socketd.server.ServerBase;
import org.noear.socketd.server.ServerConfig;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class WsBioServer extends ServerBase<WsBioExchanger> {
    private  SocketServerImpl server;
    public WsBioServer(ServerConfig serverConfig){
        super(serverConfig, new WsBioExchanger());
    }

    @Override
    public void start() throws IOException {
        if (config().getHost() != null) {
            server = new SocketServerImpl(config().getPort(),this);
        } else {
            server = new SocketServerImpl(config().getHost(), config().getPort(), this);
        }

        server.start();
    }

    @Override
    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
}
