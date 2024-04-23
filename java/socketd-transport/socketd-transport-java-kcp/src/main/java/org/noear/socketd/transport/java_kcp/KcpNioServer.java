package org.noear.socketd.transport.java_kcp;

import com.backblaze.erasure.FecAdapt;
import kcp.ChannelConfig;
import kcp.KcpServer;
import kcp.Ukcp;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.java_kcp.impl.ServerKcpListener;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.RunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author noear
 * @since 2.1
 */
public class KcpNioServer extends ServerBase<KcpNioChannelAssistant> implements ChannelSupporter<Ukcp> {
    private static final Logger log = LoggerFactory.getLogger(KcpNioServer.class);

    public KcpNioServer(ServerConfig config) {
        super(config, new KcpNioChannelAssistant(config));
    }

    private KcpServer server;

    @Override
    public String getTitle() {
        return "kcp/nio/java-kcp/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        ChannelConfig channelConfig = new ChannelConfig();

        channelConfig.nodelay(true, 40, 2, true);
        channelConfig.setSndwnd(512);
        channelConfig.setRcvwnd(512);
        channelConfig.setMtu(512);
        channelConfig.setFecAdapt(new FecAdapt(3, 1));
        channelConfig.setAckNoDelay(true);
        channelConfig.setUseConvChannel(true);
        channelConfig.setCrc32Check(true);

        if (getConfig().getIdleTimeout() > 0) {
            channelConfig.setTimeoutMillis(getConfig().getIdleTimeout());
        }

        server = new KcpServer();
        server.init(new ServerKcpListener(this), channelConfig, getConfig().getPort());

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

        if (server != null) {
            RunUtils.runAndTry(() -> Thread.sleep(100));
            server.stop();
        }
    }
}
