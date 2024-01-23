package org.noear.socketd.transport.neta.tcp;

import net.hasor.cobble.concurrent.future.Future;
import net.hasor.cobble.concurrent.future.FutureListener;
import net.hasor.neta.channel.CobbleSocket;
import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.PipelineFactory;
import net.hasor.neta.channel.SoConfig;
import net.hasor.neta.handler.PipeInitializer;
import net.hasor.neta.handler.codec.LimitFrameHandler;
import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.neta.tcp.impl.ClientPipeListener;
import org.noear.socketd.transport.neta.tcp.impl.FrameDecoder;
import org.noear.socketd.transport.neta.tcp.impl.FrameEncoder;
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

    private CobbleSocket real;

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

        PipelineFactory pipeline = PipeInitializer.builder()
                .nextToDecoder(new LimitFrameHandler(Constants.MAX_SIZE_FRAME))
                .nextTo(decoder,encoder)
                .bindReceive(pipeListener).build();

        SoConfig soConfig = new SoConfig();
        soConfig.setNetlog(true);
        real = new CobbleSocket(soConfig);


        try {
            Future<NetChannel> connect = real.connect(getConfig().getHost(), getConfig().getPort(), pipeline);
            connect.onCompleted(f -> {
                ChannelInternal channel=null;
                try{
                     channel = f.get().findPipeContext(ChannelInternal.class);
                }catch (Exception e){
                }
                //开始握手
                try{
                    channel.sendConnect(client.getConfig().getUrl(), client.getConfig().getMetaMap());
                }catch (Exception e){
                    channel.doOpenFuture(false,e);
                }
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
            throw new SocketdConnectionException("Connection timeout: " + client.getConfig().getLinkUrl());
        } catch (Throwable e) {
            close();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketdConnectionException("Connection failed: " + client.getConfig().getLinkUrl(), e);
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
