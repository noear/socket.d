package org.noear.socketd.cluster;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.*;
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

    private ClientConnectHandler connectHandler;
    private ClientHeartbeatHandler heartbeatHandler;
    private ClientConfigHandler configHandler;
    private Listener listener;

    public ClusterClient(String... serverUrls) {
        this.serverUrls = serverUrls;
    }

    @Override
    public Client connectHandler(ClientConnectHandler connectHandler) {
        this.connectHandler = connectHandler;
        return this;
    }

    @Override
    public Client heartbeatHandler(ClientHeartbeatHandler heartbeatHandler) {
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
     * 打开会话
     */
    @Override
    public ClientSession open() {
        try {
            return openDo(false);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 打开会话或出异常
     */
    @Override
    public ClientSession openOrThow() throws IOException {
        return openDo(true);
    }

    private ClientSession openDo(boolean isThow) throws IOException {
        List<ClientSession> sessionList = new ArrayList<>();
        ExecutorService exchangeExecutor = null;

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

                if (connectHandler != null) {
                    client.connectHandler(connectHandler);
                }

                if (heartbeatHandler != null) {
                    client.heartbeatHandler(heartbeatHandler);
                }

                //复用交换执行器（省点线程数）
                if (exchangeExecutor == null) {
                    exchangeExecutor = client.getConfig().getExchangeExecutor();
                } else {
                    client.getConfig().exchangeExecutor(exchangeExecutor);
                }

                if (isThow) {
                    sessionList.add(client.openOrThow());
                } else {
                    sessionList.add(client.open());
                }
            }
        }

        return new ClusterClientSession(sessionList);
    }
}
