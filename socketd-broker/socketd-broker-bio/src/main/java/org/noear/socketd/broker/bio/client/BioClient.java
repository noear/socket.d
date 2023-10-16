package org.noear.socketd.broker.bio.client;

import org.noear.socketd.client.Client;
import org.noear.socketd.client.Connector;

/**
 * @author noear 2023/10/13 created
 */
public class BioClient implements Client {
    @Override
    public Connector create(String url, boolean autoReconnect) {
        return null;
    }
}
