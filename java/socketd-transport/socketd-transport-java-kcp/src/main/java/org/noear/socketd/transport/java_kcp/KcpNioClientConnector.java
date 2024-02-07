package org.noear.socketd.transport.java_kcp;

import com.backblaze.erasure.FecAdapt;
import kcp.ChannelConfig;
import kcp.KcpClient;
import kcp.Ukcp;
import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.exception.SocketDTimeoutException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.java_kcp.impl.ClientKcpListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;
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

        //相当于，同一个IP下的通道号
        channelConfig.setConv(Math.abs(new Random().nextInt()));

        channelConfig.setFecAdapt(new FecAdapt(3, 1));
        channelConfig.setCrc32Check(true);
        //channelConfig.setAckMaskSize(32);

        if (client.getConfig().getIdleTimeout() > 0) {
            channelConfig.setTimeoutMillis(client.getConfig().getIdleTimeout());
        }

        KcpClient kcpClient = new KcpClient();
        kcpClient.init(channelConfig);

        ClientKcpListener kcpListener = new ClientKcpListener(client);
        InetSocketAddress kcpAddress = new InetSocketAddress(client.getConfig().getHost(), client.getConfig().getPort());


        try {
            real = kcpClient.connect(kcpAddress, channelConfig, kcpListener);

            //等待握手结果
            ClientHandshakeResult handshakeResult = kcpListener.getHandshakeFuture().get(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getThrowable() != null) {
                throw handshakeResult.getThrowable();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketDTimeoutException("Connection timeout: " + client.getConfig().getLinkUrl());
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
            if (real != null) {
                real.close();
            }
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}