package org.noear.socketd.cluster;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfigHandler;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.Utils;

import java.io.IOException;

/**
 * 集群客户端实现
 *
 * @author noear
 */
public class ClusterClientImpl implements ClusterClient{
    private final String[] serverUrls;

    private ClientConfigHandler configHandler;
    private Listener listener;

    public ClusterClientImpl(String... serverUrls) {
        this.serverUrls = serverUrls;
    }

    /**
     * 配置
     */
    @Override
    public ClusterClient config(ClientConfigHandler configHandler) {
        this.configHandler = configHandler;
        return this;
    }

    /**
     * 监听
     */
    @Override
    public ClusterClient listen(Listener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 打开
     */
    @Override
    public SessionSender open() throws IOException {
        ClusterSessionSender clusterSender = new ClusterSessionSender();

        for (String urls : serverUrls) {
            for (String url : urls.split(",")) {
                url = url.trim();
                if (Utils.isEmpty(url)) {
                    continue;
                }

                Client client = SocketD.createClient(url);
                if (listener != null) {
                    client.listen(listener);
                }
                if (configHandler != null) {
                    client.config(configHandler);
                }

                clusterSender.addSession(client.open());
            }
        }

        return clusterSender;
    }
}
