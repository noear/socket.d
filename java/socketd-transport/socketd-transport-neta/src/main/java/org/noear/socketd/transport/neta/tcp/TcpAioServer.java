package org.noear.socketd.transport.neta.tcp;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.NetManager;
import net.hasor.neta.channel.ProtoInitializer;
import net.hasor.neta.channel.SoConfig;
import net.hasor.neta.handler.ProtoHelper;
import net.hasor.neta.handler.codec.LengthFieldBasedFrameHandler;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.neta.tcp.impl.FrameDecoder;
import org.noear.socketd.transport.neta.tcp.impl.FrameEncoder;
import org.noear.socketd.transport.neta.tcp.impl.ServerPipeListener;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author noear
 * @since 2.3
 */
public class TcpAioServer extends ServerBase<TcpAioChannelAssistant> implements ChannelSupporter<NetChannel> {
    private static final Logger     log = LoggerFactory.getLogger(TcpAioServer.class);
    private              NetManager server;

    public TcpAioServer(ServerConfig config) {
        super(config, new TcpAioChannelAssistant());
    }

    @Override
    public String getTitle() {
        return "tcp/aio/neta 1.0/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        FrameDecoder decoder = new FrameDecoder(this.getConfig(), this);
        FrameEncoder encoder = new FrameEncoder(this.getConfig(), this);
        ServerPipeListener listener = new ServerPipeListener(this);

        ProtoInitializer initializer = ctx -> ProtoHelper.builder()//
                .nextDecoder(new LengthFieldBasedFrameHandler(0, ByteOrder.BIG_ENDIAN, 4, 0, -4, Constants.MAX_SIZE_FRAME))//
                .nextDuplex(decoder, encoder)//
                .nextDecoder(listener).build();

        SoConfig soConfig = new SoConfig();
        soConfig.setSoKeepAlive(false);
        soConfig.setNetlog(true);
        server = new NetManager(soConfig);

        if (StrUtils.isEmpty(getConfig().getHost())) {
            server.listen(getConfig().getPort(), initializer);
        } else {
            server.listen(getConfig().getHost(), getConfig().getPort(), initializer);
        }

        log.info("Socket.D server started: {server=" + getConfig().getLocalUrl() + "}");

        return this;
    }

    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        super.stop();

        try {
            if (server != null) {
                server.shutdown();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Server stop error", e);
            }
        }
    }
}