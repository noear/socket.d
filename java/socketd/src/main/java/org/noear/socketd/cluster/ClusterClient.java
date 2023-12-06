package org.noear.socketd.cluster;

import org.noear.socketd.transport.client.ClientConfigHandler;
import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.SessionSender;

import java.io.IOException;

/**
 * 集群客户端
 *
 * @author noear
 * @since 2.1
 */
public interface ClusterClient {
    /**
     * 配置
     */
    ClusterClient config(ClientConfigHandler configHandler);

    /**
     * 监听
     */
    ClusterClient listen(Listener listener);

    /**
     * 打开
     */
    SessionSender open() throws IOException;
}
