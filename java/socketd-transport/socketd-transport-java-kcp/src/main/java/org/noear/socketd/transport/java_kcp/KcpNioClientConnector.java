package org.noear.socketd.transport.java_kcp;

import com.backblaze.erasure.FecAdapt;
import kcp.ChannelConfig;
import kcp.KcpClient;
import kcp.Ukcp;
import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.java_kcp.impl.ClientKcpListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 2.1
 */
public class KcpNioClientConnector extends ClientConnectorBase<KcpNioClient> {
    private static final Logger log = LoggerFactory.getLogger(KcpNioClientConnector.class);

    private Ukcp real;

    public KcpNioClientConnector(KcpNioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        //关闭之前的资源
        close();

        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.nodelay(true, 40, 2, true);
        channelConfig.setSndwnd(512);
        channelConfig.setRcvwnd(512);
        channelConfig.setMtu(512);
        channelConfig.setAckNoDelay(true);
        channelConfig.setConv(55);

        channelConfig.setFecAdapt(new FecAdapt(3, 1));
        channelConfig.setCrc32Check(true);
        //channelConfig.setTimeoutMillis(10000);
        //channelConfig.setAckMaskSize(32);
        KcpClient kcpClient = new KcpClient();
        kcpClient.init(channelConfig);

        ClientKcpListener kcpListener = new ClientKcpListener(client);
        InetSocketAddress kcpAddress = new InetSocketAddress(client.config().getHost(), client.config().getPort());


        try {
            real = kcpClient.connect(kcpAddress, channelConfig, kcpListener);

            //等待握手结果
            ClientHandshakeResult handshakeResult = kcpListener.getHandshakeFuture().get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getThrowable() != null) {
                throw handshakeResult.getThrowable();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketdTimeoutException("Connection timeout: " + client.config().getLinkUrl());
        } catch (Throwable e) {
            close();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketdConnectionException(e);
            }
        }
    }

    @Override
    public void close() {
        try {
            if (real != null) {
                real.close();
            }
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}