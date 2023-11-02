package org.noear.socketd.broker.java_websocket;

import org.noear.socketd.broker.java_websocket.impl.WebSocketClientImpl;
import org.noear.socketd.client.ClientConnectorBase;
import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.core.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Ws-Bio 客户端连接器实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class WsBioClientConnector extends ClientConnectorBase<WsBioClient> {
    private static final Logger log = LoggerFactory.getLogger(WsBioClientConnector.class);

    private WebSocketClientImpl real;

    public WsBioClientConnector(WsBioClient client) {
        super(client);
    }

    @Override
    public Channel connect() throws Exception {
        log.debug("Start connecting to: {}", client.config().getUrl());

        //处理自定义架构的影响
        String wsUrl = client.config().getUrl().replace("-java://","://");
        real = new WebSocketClientImpl(URI.create(wsUrl), client);

        //支持 ssl
        if (client.config().getSslContext() != null) {
            real.setSocketFactory(client.config().getSslContext().getSocketFactory());
        }

        try {
            if (real.connectBlocking(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS)) {
                return real.getChannel();
            } else {
                throw new SocketdConnectionException("Connection fail: " + client.config().getUrl());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        if (real == null) {
            return;
        }

        try {
            real.close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}
