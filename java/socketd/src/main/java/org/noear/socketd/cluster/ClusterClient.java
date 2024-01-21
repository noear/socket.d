package org.noear.socketd.cluster;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.Client;
import org.noear.socketd.transport.client.ClientConfigHandler;
import org.noear.socketd.transport.client.ClientInternal;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.StrUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 集群客户端
 *
 * @author noear
 */
public class ClusterClient implements Client {
    private final String[] serverUrls;

    private HeartbeatHandler heartbeatHandler;
    private ClientConfigHandler configHandler;
    private Listener listener;

    public ClusterClient(String... serverUrls) {
        this.serverUrls = serverUrls;
    }

    @Override
    public Client heartbeatHandler(HeartbeatHandler heartbeatHandler) {
        this.heartbeatHandler = heartbeatHandler;
        return this;
    }

    /**
     * 配置
     */
    @Override
    public Client config(ClientConfigHandler configHandler) {
        this.configHandler = configHandler;
        return this;
    }

    /**
     * 监听
     */
    @Override
    public Client listen(Listener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 打开
     */
    @Override
    public ClientSession open() throws IOException {
        return openDo(true);
    }

    @Override
    public ClientSession openAndTry() throws IOException {
        return openDo(false);
    }

    private ClientSession openDo(boolean isThow) throws IOException {
        List<ClientSession> sessionList = new ArrayList<>();
        ExecutorService channelExecutor = null;

        for (String urls : serverUrls) {
            for (String url : urls.split(",")) {
                url = url.trim();
                if (StrUtils.isEmpty(url)) {
                    continue;
                }

                ClientInternal client = (ClientInternal) SocketD.createClient(url);

                if (listener != null) {
                    client.listen(listener);
                }

                if (configHandler != null) {
                    client.config(configHandler);
                }

                if (heartbeatHandler != null) {
                    client.heartbeatHandler(heartbeatHandler);
                }

                //复用通道执行器（省点线程数）
                if (channelExecutor == null) {
                    channelExecutor = client.getConfig().getChannelExecutor();
                } else {
                    client.getConfig().channelExecutor(channelExecutor);
                }

                if (isThow) {
                    sessionList.add(client.open());
                } else {
                    sessionList.add(client.openAndTry());
                }
            }
        }

        return new ClusterClientSession(sessionList);
    }
}
