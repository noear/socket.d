package org.noear.socketd.broker.bio.client;

import org.noear.socketd.client.Client;
import org.noear.socketd.client.Connector;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class BioClient implements Client {
    private BioClientConfig clientConfig;

    public BioClient(BioClientConfig config) {
        this.clientConfig = config;
    }

    @Override
    public Connector create(String url) throws IOException {
        return new BioConnector(url, clientConfig);
    }
}
