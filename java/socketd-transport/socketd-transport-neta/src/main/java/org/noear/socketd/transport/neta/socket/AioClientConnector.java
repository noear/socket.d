package org.noear.socketd.transport.neta.socket;

import net.hasor.cobble.concurrent.future.Future;
import net.hasor.neta.channel.*;
import net.hasor.neta.handler.codec.LengthFieldBasedFrameHandler;
import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.neta.codec.FrameDecoder;
import org.noear.socketd.transport.neta.codec.FrameEncoder;
import org.noear.socketd.transport.neta.listener.ClientListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 2.3
 */
public abstract class AioClientConnector extends ClientConnectorBase<AioClient> {
    private static final Logger log = LoggerFactory.getLogger(AioClientConnector.class);

    private NetManager real;

    public AioClientConnector(AioClient client) {
        super(client);
    }

    protected abstract Future<NetChannel> connectTo(NetManager neta, InetSocketAddress remoteAddr, ProtoInitializer initializer);

    @Override
    public ChannelInternal connect() throws IOException {
        //关闭之前的资源
        close();

        ClientListener pipeListener = new ClientListener(client);
        InetSocketAddress remoteAddr = new InetSocketAddress(getConfig().getHost(), getConfig().getPort());
        NetConfig netConfig = new NetConfig();

        netConfig.setPrintLog(true);
        real = new NetManager(netConfig);
        real.subscribe(PlayLoad::isInbound, pipeListener);

        try {
            Future<NetChannel> connect = this.connectTo(this.real, remoteAddr, ctx -> {
                // ssl
                if (AioSslHelper.isUsingSSL(getConfig())) {
                    ctx.addLast("SSL", AioSslHelper.createSSL(getConfig()));
                }
                // frame
                ctx.addLastDecoder("Frame", new LengthFieldBasedFrameHandler(0, ByteOrder.BIG_ENDIAN, 4, 0, -4, Constants.MAX_SIZE_FRAME));
                // codec
                FrameDecoder decoder = new FrameDecoder(client.getConfig(), client);
                FrameEncoder encoder = new FrameEncoder(client.getConfig(), client);
                ctx.addLast("Socket.D", decoder, encoder);
            });

            connect.onCompleted(f -> {
                //开始握手
                ChannelInternal channel = f.getResult().findProtoContext(ChannelInternal.class);
                try {
                    channel.sendConnect(client.getConfig().getUrl(), client.getConfig().getMetaMap());
                } catch (Exception e) {
                    channel.doOpenFuture(false, e);
                }
            }).onFailed(f -> {
                ChannelInternal channel = f.getResult().findProtoContext(ChannelInternal.class);
                channel.doOpenFuture(false, f.getCause());
            });

            //等待握手结果
            ClientHandshakeResult handshakeResult = pipeListener.getHandshakeFuture().get(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);

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
            if (real != null) {
                real.shutdown();
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("Client connector close error", e);
            }
        }
    }
}
