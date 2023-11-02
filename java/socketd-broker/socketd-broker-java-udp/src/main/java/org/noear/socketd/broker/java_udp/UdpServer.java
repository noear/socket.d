package org.noear.socketd.broker.java_udp;

import org.noear.socketd.broker.java_udp.impl.DatagramFrame;
import org.noear.socketd.broker.java_udp.impl.DatagramTagert;
import org.noear.socketd.core.Channel;
import org.noear.socketd.core.impl.ChannelDefault;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerBase;
import org.noear.socketd.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Udp-Bio 服务端实现（支持 ssl, host）
 *
 * @author Urara
 * @since 2.0
 */
public class UdpServer extends ServerBase<UdpChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(UdpServer.class);

    private Map<String, Channel> channelMap = new HashMap<>();
    private DatagramSocket server;
    private Thread serverThread;
    private ExecutorService serverExecutor;

    public UdpServer(ServerConfig config) {
        super(config, new UdpChannelAssistant(config));
    }

    private DatagramSocket createServer() throws IOException {
        return new DatagramSocket(config().getPort());
    }

    @Override
    public Server start() throws IOException {
        if (serverThread != null) {
            throw new IllegalStateException("Server started");
        }

        if (serverExecutor == null) {
            serverExecutor = Executors.newFixedThreadPool(config().getCoreThreads());
        }

        server = createServer();

        serverThread = new Thread(() -> {
            while (true) {
                try {
                    DatagramFrame datagramFrame = assistant().read(server);
                    if (datagramFrame == null) {
                        continue;
                    }

                    Channel channel = getChannel(datagramFrame);

                    try {
                        serverExecutor.submit(() -> {
                            try {
                                processor().onReceive(channel, datagramFrame.getFrame());
                            } catch (Throwable e) {
                                log.debug("{}", e);
                            }
                        });
                    } catch (Throwable e) {
                        log.debug("{}", e);
                    }
                } catch (IOException e) {
                    if (server.isClosed()) {
                        //说明被手动关掉了
                        return;
                    }

                    log.debug("{}", e);
                }
            }
        });
        serverThread.start();

        log.info("Server started: {server=udp://127.0.0.1:" + config().getPort() + "}");

        return this;
    }

    private Channel getChannel(DatagramFrame datagramFrame) {
        String addressAndPort = datagramFrame.getPacketAddress();
        Channel channel0 = channelMap.get(addressAndPort);

        if (channel0 == null) {
            DatagramTagert tagert = new DatagramTagert(server, datagramFrame.getPacket(), false);
            channel0 = new ChannelDefault<>(tagert, config(), assistant());
            channelMap.put(addressAndPort, channel0);
        }

        return channel0;
    }


    @Override
    public void stop() {
        if (server == null || server.isClosed()) {
            return;
        }
        try {
            server.close();
            serverThread.stop();
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}