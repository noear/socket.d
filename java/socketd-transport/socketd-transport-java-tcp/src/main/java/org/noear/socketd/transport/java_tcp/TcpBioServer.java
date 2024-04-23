package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Tcp-Bio 服务端实现（支持 ssl, host）
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioServer extends ServerBase<TcpBioChannelAssistant> implements ChannelSupporter<Socket> {
    private static final Logger log = LoggerFactory.getLogger(TcpBioServer.class);

    private ServerSocket server;
    private ExecutorService serverExecutor;

    public TcpBioServer(ServerConfig config) {
        super(config, new TcpBioChannelAssistant(config));
    }

    /**
     * 创建 server（支持 ssl, host）
     */
    private ServerSocket createServer() throws IOException {
        if (getConfig().getSslContext() == null) {
            if (StrUtils.isEmpty(getConfig().getHost())) {
                return new ServerSocket(getConfig().getPort());
            } else {
                return new ServerSocket(getConfig().getPort(), 50, InetAddress.getByName(getConfig().getHost()));
            }
        } else {
            if (StrUtils.isEmpty(getConfig().getHost())) {
                return getConfig().getSslContext().getServerSocketFactory().createServerSocket(getConfig().getPort());
            } else {
                return getConfig().getSslContext().getServerSocketFactory().createServerSocket(getConfig().getPort(), 50, InetAddress.getByName(getConfig().getHost()));
            }
        }
    }

    @Override
    public String getTitle() {
        return "tcp/bio/java-tcp/" + SocketD.version();
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
            Socket socketTmp = null;
            try {
                Socket socket = socketTmp = server.accept();

                //闲置超时
                if (getConfig().getIdleTimeout() > 0L) {
                    //单位：毫秒
                    socket.setSoTimeout((int) getConfig().getIdleTimeout());
                }

                //读缓冲
                if (getConfig().getReadBufferSize() > 0) {
                    socket.setReceiveBufferSize(getConfig().getReadBufferSize());
                }

                //写缓冲
                if (getConfig().getWriteBufferSize() > 0) {
                    socket.setSendBufferSize(getConfig().getWriteBufferSize());
                }

                serverExecutor.submit(() -> {
                    try {
                        ChannelInternal channel = new ChannelDefault<>(socket, this);
                        receive(channel, socket);
                    } catch (Throwable e) {
                        if (log.isWarnEnabled()) {
                            log.warn("Server receive error", e);
                        }
                        close(socket);
                    }
                });
            } catch (RejectedExecutionException e) {
                if (socketTmp != null) {
                    log.warn("Server thread pool is full", e);
                    RunUtils.runAndTry(socketTmp::close);
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

    /**
     * 接收数据
     */
    private void receive(ChannelInternal channel, Socket socket) {
        while (true) {
            try {
                try {
                    if (socket.isClosed()) {
                        getProcessor().onClose(channel);
                        break;
                    }

                    Frame frame = getAssistant().read(socket);
                    if (frame != null) {
                        getProcessor().onReceive(channel, frame);
                    } else {
                        //休息10ms（避免cpu过高）
                        Thread.sleep(10);
                    }
                } catch (SocketTimeoutException e) {
                    //说明 idleTimeout
                    if (log.isDebugEnabled()) {
                        log.debug("Server channel idle timeout, remoteIp={}", socket.getRemoteSocketAddress());
                    }
                    //注意：socket 客户端无法感知关闭，需要发消息通知
                    channel.sendClose(Constants.CLOSE1001_PROTOCOL_CLOSE);
                    throw e;
                }
            } catch (IOException e) {
                //如果是 SocketTimeoutException，说明 idleTimeout
                getProcessor().onError(channel, e);
                getProcessor().onClose(channel);
                close(socket);
                break;
            } catch (Throwable e) {
                getProcessor().onError(channel, e);
            }
        }
    }


    private void close(Socket socket) {
        try {
            socket.close();
        } catch (Throwable e) {
            log.debug("Server socket close error", e);
        }
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