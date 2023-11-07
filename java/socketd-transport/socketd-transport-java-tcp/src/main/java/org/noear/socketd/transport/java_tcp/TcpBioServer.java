package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tcp-Bio 服务端实现（支持 ssl, host）
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioServer extends ServerBase<TcpBioChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(TcpBioServer.class);

    private ServerSocket server;
    private Thread serverThread;
    private ExecutorService serverExecutor;

    public TcpBioServer(ServerConfig config) {
        super(config, new TcpBioChannelAssistant(config));
    }

    /**
     * 创建 server（支持 ssl, host）
     */
    private ServerSocket createServer() throws IOException {
        if (config().getSslContext() == null) {
            if (Utils.isEmpty(config().getHost())) {
                return new ServerSocket(config().getPort());
            } else {
                return new ServerSocket(config().getPort(), 50, InetAddress.getByName(config().getHost()));
            }
        } else {
            if (Utils.isEmpty(config().getHost())) {
                return config().getSslContext().getServerSocketFactory().createServerSocket(config().getPort());
            } else {
                return config().getSslContext().getServerSocketFactory().createServerSocket(config().getPort(), 50, InetAddress.getByName(config().getHost()));
            }
        }
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Server started");
        } else {
            isStarted = true;
        }

        if (serverExecutor == null) {
            serverExecutor = config().getExecutor();
            if (serverExecutor == null) {
                serverExecutor = Executors.newFixedThreadPool(config().getMaxThreads());
            }
        }

        server = createServer();

        //读取数据超时，即 source.getInputStream() 超时？
        if(config().getIdleTimeout() > 0L) {
            //单位：毫秒
            server.setSoTimeout((int) config().getIdleTimeout());
        }

        serverThread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = server.accept();

                    serverExecutor.submit(() -> {
                        try {
                            Channel channel = new ChannelDefault<>(socket, config(), assistant());
                            receive(channel, socket);
                        } catch (Throwable e) {
                            log.debug("{}", e);
                            close(socket);
                        }
                    });
                } catch (Throwable e) {
                    if (server.isClosed()) {
                        //说明被手动关掉了
                        return;
                    }

                    log.debug("{}", e);
                }
            }
        });

        serverThread.start();

        log.info("Server started: {server=" + config().getLocalUrl() + "}");

        return this;
    }

    private void receive(Channel channel, Socket socket) {
        while (true) {
            try {
                if (socket.isClosed()) {
                    processor().onClose(channel.getSession());
                    break;
                }

                Frame frame = assistant().read(socket);
                if (frame != null) {
                    processor().onReceive(channel, frame);
                }
            }
            catch (SocketException e) {
                //如果是 java.net.ConnectException，说明 idleTimeout
                processor().onError(channel.getSession(), e);
                processor().onClose(channel.getSession());
                close(socket);
                break;
            } catch (Throwable e) {
                processor().onError(channel.getSession(), e);
            }
        }
    }


    private void close(Socket socket) {
        try {
            socket.close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }

    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        try {
            server.close();
            serverThread.interrupt();
            serverExecutor.shutdown();
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}