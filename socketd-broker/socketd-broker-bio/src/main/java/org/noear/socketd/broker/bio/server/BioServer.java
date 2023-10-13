package org.noear.socketd.broker.bio.server;

import org.noear.socketd.Listener;
import org.noear.socketd.server.Server;

import java.io.IOException;

/**
 * @author noear 2023/10/13 created
 */
public class BioServer implements Server {
    private BioServerConfig serverConfig;

    public BioServer(BioServerConfig serverConfig){
        this.serverConfig = serverConfig;
    }

    @Override
    public void start(Listener listener) throws IOException {

    }

    @Override
    public void stop() throws IOException {

    }
}
