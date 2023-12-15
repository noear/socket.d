package org.noear.socketd.transport.client;

import org.noear.socketd.exception.SocketdChannelException;
import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.internal.HeartbeatHandlerDefault;
import org.noear.socketd.transport.core.StreamBase;
import org.noear.socketd.utils.RunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

/**
 * 客户端通道
 *
 * @author noear
 * @since 2.0
 */
public class ClientChannel extends ChannelBase implements Channel {
    private static final Logger log = LoggerFactory.getLogger(ClientChannel.class);

    //连接器
    private final ClientConnector connector;
    //真实通道
    private Channel real;
    //心跳处理
    private HeartbeatHandler heartbeatHandler;
    //心跳调度
    private ScheduledFuture<?> heartbeatScheduledFuture;

    public ClientChannel(Channel real, ClientConnector connector) {
        super(real.getConfig());
        this.connector = connector;
        this.real = real;
        this.heartbeatHandler = connector.heartbeatHandler();

        if (heartbeatHandler == null) {
            heartbeatHandler = new HeartbeatHandlerDefault();
        }

        initHeartbeat();
    }

    /**
     * 初始化心跳（关闭后，手动重链时也会用到）
     */
    private void initHeartbeat() {
        if (heartbeatScheduledFuture != null) {
            heartbeatScheduledFuture.cancel(false);
        }

        if (connector.autoReconnect()) {
            heartbeatScheduledFuture = RunUtils.delayAndRepeat(() -> {
                try {
                    heartbeatHandle();
                } catch (Exception e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Client channel heartbeat error", e);
                    }
                }
            }, connector.heartbeatInterval());
        }
    }

    /**
     * 是否有效
     */
    @Override
    public boolean isValid() {
        if (real == null) {
            return false;
        } else {
            return real.isValid();
        }
    }

    /**
     * 是否已关闭
     */
    @Override
    public int isClosed() {
        if (real == null) {
            return 0;
        } else {
            return real.isClosed();
        }
    }

    /**
     * 获取远程地址
     */
    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        if (real == null) {
            return null;
        } else {
            return real.getRemoteAddress();
        }
    }

    /**
     * 获取本地地址
     */
    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        if (real == null) {
            return null;
        } else {
            return real.getLocalAddress();
        }
    }

    /**
     * 心跳处理
     */
    private void heartbeatHandle() throws IOException {
        if (real != null) {
            //说明握手未成
            if (real.getHandshake() == null) {
                return;
            }

            //手动关闭
            if (real.isClosed() == Constants.CLOSE4_USER) {
                if (log.isDebugEnabled()) {
                    log.debug("Client channel is closed (pause heartbeat), sessionId={}", getSession().sessionId());
                }
                return;
            }
        }

        try {
            prepareCheck();

            heartbeatHandler.heartbeat(getSession());
        } catch (SocketdException e) {
            throw e;
        } catch (Throwable e) {
            if (connector.autoReconnect()) {
                real.close(Constants.CLOSE3_ERROR);
                real = null;
            }

            throw new SocketdChannelException(e);
        }
    }

    /**
     * 发送
     *
     * @param frame  帧
     * @param stream 流（没有则为 null）
     */
    @Override
    public void send(Frame frame, StreamBase stream) throws IOException {
        Asserts.assertClosedByUser(real);

        try {
            prepareCheck();

            real.send(frame, stream);
        } catch (SocketdException e) {
            throw e;
        } catch (Throwable e) {
            if (connector.autoReconnect()) {
                real.close(Constants.CLOSE3_ERROR);
                real = null;
            }
            throw new SocketdChannelException(e);
        }
    }

    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    @Override
    public void retrieve(Frame frame) {
        real.retrieve(frame);
    }

    /**
     * 获取会话
     */
    @Override
    public Session getSession() {
        return real.getSession();
    }

    @Override
    public CompletableFuture<Boolean> onOpenFuture() {
        return real.onOpenFuture();
    }


    @Override
    public void reconnect() throws IOException {
        initHeartbeat();

        prepareCheck();
    }

    @Override
    public void onError(Throwable error) {
        real.onError(error);
    }

    /**
     * 关闭
     */
    @Override
    public void close(int code) {
        RunUtils.runAndTry(() -> heartbeatScheduledFuture.cancel(true));
        RunUtils.runAndTry(() -> connector.close());
        RunUtils.runAndTry(() -> real.close(code));
    }


    /**
     * 预备检
     *
     * @return 是否为新链接
     */
    private boolean prepareCheck() throws IOException {
        if (real == null || real.isValid() == false) {
            real = connector.connect();

            return true;
        } else {
            return false;
        }
    }
}