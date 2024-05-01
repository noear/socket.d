package org.noear.socketd.transport.java_websocket;

import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.java_websocket.impl.WebSocketClientImpl;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Ws-Bio 客户端连接器实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class WsNioClientConnector extends ClientConnectorBase<WsNioClient> {
    private static final Logger log = LoggerFactory.getLogger(WsNioClientConnector.class);

    private WebSocketClientImpl real;

    public WsNioClientConnector(WsNioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        //关闭之前的资源
        close();

        //处理自定义架构的影响（重连时，新建实例比原生重链接口靠谱）
        String wsUrl = client.getConfig().getUrl().replace("-java://", "://");
        real = new WebSocketClientImpl(URI.create(wsUrl), client);

        //支持 ssl
        if (client.getConfig().getSslContext() != null) {
            real.setSocketFactory(client.getConfig().getSslContext().getSocketFactory());
        }

        //闲置超时
        if (client.getConfig().getIdleTimeout() > 0L) {
            //单位：毫秒
            real.setConnectionLostTimeout((int) (client.getConfig().getIdleTimeout() / 1000L));
        }

        real.setReuseAddr(true);
        real.connect();

        try {
            //等待握手结果
            ClientHandshakeResult handshakeResult = real.getHandshakeFuture().get(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getThrowable() != null) {
                throw handshakeResult.getThrowable();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketDConnectionException("Connection timeout: " + client.getConfig().getLinkUrl());
        } catch (Throwable e) {
            close();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketDConnectionException("Connection failed: " + client.getConfig().getLinkUrl(), e);
            }
        }
    }

    @Override
    public void close() {
        try {
            if(real != null && real.isOpen()) {
                real.close();
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("Client connector close error", e);
            }
        }
    }
}