package org.noear.socketd.broker.websocket;

import org.noear.socketd.server.ServerBase;
import org.noear.socketd.server.ServerConfig;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class WsServer extends ServerBase {
    public WsServer(ServerConfig serverConfig){
        super(serverConfig, new WsExchanger());
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() throws IOException {

    }
}
