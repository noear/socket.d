package org.noear.socketd.transport.neta.tcp;

import net.hasor.cobble.concurrent.future.Future;
import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.NetaSocket;
import net.hasor.neta.channel.PipeInitializer;
import net.hasor.neta.channel.SoConfig;
import net.hasor.neta.handler.PipeHelper;
import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.neta.tcp.impl.ClientPipeListener;
import org.noear.socketd.transport.neta.tcp.impl.FrameDecoder;
import org.noear.socketd.transport.neta.tcp.impl.FrameEncoder;
import org.noear.socketd.transport.neta.tcp.impl.FixedLengthFrameHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 2.3
 */
public class TcpAioClientConnector extends ClientConnectorBase<TcpAioClient> {
    private static final Logger log = LoggerFactory.getLogger(TcpAioClientConnector.class);

    private NetaSocket real;

    public TcpAioClientConnector(TcpAioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        //关闭之前的资源
        close();

        FrameDecoder decoder = new FrameDecoder(client.getConfig(), client);
        FrameEncoder encoder = new FrameEncoder(client.getConfig(), client);
        ClientPipeListener pipeListener = new ClientPipeListener(client);

        PipeInitializer initializer = ctx -> PipeHelper.builder()
                .nextDecoder(new FixedLengthFrameHandler(Constants.MAX_SIZE_FRAME))
                .nextDuplex(decoder,encoder)
                .nextDecoder(pipeListener).build();

        SoConfig soConfig = new SoConfig();
        soConfig.setNetlog(true);
        real = new NetaSocket(soConfig);

        try {
            Future<NetChannel> connect = real.connect(getConfig().getHost(), getConfig().getPort(), initializer);
            connect.onCompleted(f -> {
                //开始握手
                ChannelInternal channel=f.getResult().findPipeContext(ChannelInternal.class);
                try{
                    channel.sendConnect(client.getConfig().getUrl(), client.getConfig().getMetaMap());
                }catch (Exception e){
                    channel.doOpenFuture(false,e);
                }
            }).onFailed(f -> {
                ChannelInternal channel=f.getResult().findPipeContext(ChannelInternal.class);
                channel.doOpenFuture(false,f.getCause());
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
