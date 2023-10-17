package org.noear.socketd.broker.bio;

import org.noear.socketd.protocol.*;
import org.noear.socketd.protocol.impl.ChannelDefault;
import org.noear.socketd.protocol.impl.ProcessorDefault;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author noear
 * @since 2.0
 */
public class BioServer implements Server {
    private static final Logger log = LoggerFactory.getLogger(BioServer.class);

    private ServerSocket server;
    private ServerConfig serverConfig;
    private Thread serverThread;
    private ExecutorService serverExecutor;

    private Processor processor;
    private BioExchanger exchanger;

    public BioServer(ServerConfig config) {
        this.serverConfig = config;
        this.exchanger = new BioExchanger();
    }


    @Override
    public void listen(Listener listener) {
        this.processor = new ProcessorDefault(listener);
    }

    @Override
    public void start() throws IOException {
        if (serverThread != null) {
            throw new IllegalStateException("Server started");
        }

        if (serverExecutor == null) {
            serverExecutor = Executors.newFixedThreadPool(serverConfig.getCoreThreads());
        }

        if (server == null) {
            server = new ServerSocket(serverConfig.getPort());
        }

        serverThread = new Thread(() -> {
            try {
                while (true) {
                    Socket socket = server.accept();

                    try {
                        Channel channel = new ChannelDefault<>(socket, socket::close, exchanger);

                        serverExecutor.submit(() -> {
                            receive(channel, socket);
                        });
                    } catch (Throwable e) {
                        log.debug("{}", e);
                        close(socket);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });

        serverThread.start();
    }

    private void receive(Channel channel, Socket socket) {
        while (true) {
            try {
                if (socket.isClosed()) {
                    processor.onClose(channel.getSession());
                    break;
                }

                Frame frame = exchanger.read(socket);
                if (frame != null) {
                    processor.onReceive(channel, frame);
                }
            } catch (Throwable ex) {
                processor.onError(channel.getSession(), ex);
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
    public void stop() throws IOException {
        if (server == null || server.isClosed()) {
            return;
        }

        try {
            server.close();
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}