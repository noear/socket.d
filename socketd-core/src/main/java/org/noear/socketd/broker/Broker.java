package org.noear.socketd.broker;

import org.noear.socketd.client.Client;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;

/**
 * 经纪人
 *
 * @author noear
 * @since 2.0
 */
public interface Broker<T extends ServerConfig> {
    static Broker getInstance() {
        return null;
    }

    Server createServer(T config);
    Client createClient();
}
