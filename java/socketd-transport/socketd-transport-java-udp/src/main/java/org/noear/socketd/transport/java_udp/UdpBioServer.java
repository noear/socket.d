package org.noear.socketd.transport.java_udp;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.java_udp.impl.DatagramFrame;
import org.noear.socketd.transport.java_udp.impl.DatagramTagert;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Udp-Bio 服务端实现（支持 ssl, host）
 *
 * @author Urara
 * @since 2.0
 */
public class UdpBioServer extends ServerBase<UdpBioChannelAssistant> implements ChannelSupporter<DatagramTagert> {
    private static final Logger log = LoggerFactory.getLogger(UdpBioServer.class);

    private Map<String, ChannelInternal> channelMap = new HashMap<>();
    private DatagramSocket server;
    private ExecutorService serverExecutor;

    public UdpBioServer(ServerConfig config) {
        super(config, new UdpBioChannelAssistant(config));
    }

    private DatagramSocket createServer() throws IOException {
        return new DatagramSocket(getConfig().getPort());
    }

    @Override
    public String getTitle() {
        return "udp/bio/java-udp/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        serverExecutor = Executors.newFixedThreadPool(getConfig().getExchangeThreads());
        server = createServer();

        serverExecutor.submit(this::accept);

        log.info("Socket.D server started: {server=" + getConfig().getLocalUrl() + "}");

        return this;
    }

    /**
     * 接受请求
     */
    private void accept() {
        while (true) {
            try {
                DatagramFrame datagramFrame = getAssistant().read(server);
                if (datagramFrame == null) {
                    continue;
                }

                boolean isNewConnect = datagramFrame.getFrame().flag() == Flags.Connect;
                ChannelInternal channel = getChannel(datagramFrame, isNewConnect);

                try {
                    serverExecutor.submit(() -> {
                        try {
                            getProcessor().onReceive(channel, datagramFrame.getFrame());
                        } catch (Throwable e) {
                            if (log.isWarnEnabled()) {
                                log.warn("Server receive error", e);
                            }
                        }
                    });
                } catch (RejectedExecutionException e) {
                    log.warn("Server thread pool is full", e);
                } catch (Throwable e) {
                    log.warn("Server thread pool error", e);
                }
            } catch (Throwable e) {
                if (server.isClosed()) {
                    //说明被手动关掉了
                    return;
                }

                log.warn("Server accept error", e);
            }
        }
    }

    private ChannelInternal getChannel(DatagramFrame datagramFrame, boolean isNewConnect) {
        String addressAndPort = datagramFrame.getPacketAddress();
        ChannelInternal channel0 = channelMap.get(addressAndPort);

        if (isNewConnect) {
            if (channel0 != null) {
                //如果是新连接，并且有旧的通道；先把旧的关闭
                try {
                    getProcessor().onClose(channel0);
                } catch (Throwable e) {
                    getProcessor().onError(channel0, e);
                }
                channel0 = null;
            }
        }

        if (channel0 == null) {
            DatagramTagert tagert = new DatagramTagert(server, datagramFrame.getPacket(), false);
            channel0 = new ChannelDefault<>(tagert, this);
            channelMap.put(addressAndPort, channel0);
        }

        return channel0;
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
                server.close();
            }

            if (serverExecutor != null) {
                serverExecutor.shutdown();
            }
        } catch (Exception e) {
            log.debug("Server stop error", e);
        }
    }
}