package org.noear.socketd.transport.client;

import org.noear.socketd.exception.SocketdChannelException;
import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.impl.ChannelBase;
import org.noear.socketd.transport.core.impl.HeartbeatHandlerDefault;
import org.noear.socketd.transport.core.impl.SessionDefault;
import org.noear.socketd.transport.stream.StreamInternal;
import org.noear.socketd.utils.RunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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
    //会话壳
    private final Session sessionShell;
    //真实通道
    private ChannelInternal real;
    //心跳处理
    private HeartbeatHandler heartbeatHandler;
    //心跳调度
    private ScheduledFuture<?> heartbeatScheduledFuture;
    //连接状态
    private AtomicBoolean isConnecting = new AtomicBoolean(false);

    public ClientChannel(ClientConnector connector) {
        super(connector.getConfig());
        this.connector = connector;
        this.sessionShell = new SessionDefault(this);

        if (connector.getHeartbeatHandler() == null) {
            this.heartbeatHandler = new HeartbeatHandlerDefault();
        } else {
            this.heartbeatHandler = connector.getHeartbeatHandler();
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
            heartbeatScheduledFuture = RunUtils.scheduleWithFixedDelay(() -> {
                try {
                    heartbeatHandle();
                } catch (Exception e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Client channel heartbeat error", e);
                    }
                }
            }, connector.getHeartbeatInterval(), connector.getHeartbeatInterval());
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

    @Override
    public long getLiveTime() {
        if (real == null) {
            return 0L;
        } else {
            return real.getLiveTime();
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

            //关闭并结束了或者正在关闭中
            if (Asserts.isClosedAndEnd(real) || real.isClosing()) {
                if (log.isDebugEnabled()) {
                    log.debug("Client channel is closed (pause heartbeat), sessionId={}", getSession().sessionId());
                }
                return;
            }
        }

        try {
            internalCheck();

            heartbeatHandler.heartbeat(getSession());
        } catch (SocketdException e) {
            throw e;
        } catch (Throwable e) {
            if (connector.autoReconnect()) {
                internalCloseIfError();
            }

            throw new SocketdChannelException("Client channel heartbeat failed", e);
        }
    }

    /**
     * 发送
     *
     * @param frame  帧
     * @param stream 流（没有则为 null）
     */
    @Override
    public void send(Frame frame, StreamInternal stream) throws IOException {
        Asserts.assertClosedAndEnd(real);

        try {
            internalCheck();

            if (real == null) {
                //有可能此时仍未连接
                throw new SocketdChannelException("Client channel is not connected");
            }

            real.send(frame, stream);
        } catch (SocketdException e) {
            throw e;
        } catch (Throwable e) {
            if (connector.autoReconnect()) {
                internalCloseIfError();
            }
            throw new SocketdChannelException("Client channel send failed", e);
        }
    }

    /**
     * 接收（接收答复帧）
     *
     * @param frame  帧
     * @param stream 流
     */
    @Override
    public void retrieve(Frame frame, StreamInternal stream) {
        real.retrieve(frame, stream);
    }


    /**
     * 重新连接
     */
    @Override
    public void reconnect() throws IOException {
        initHeartbeat();

        internalCheck();
    }

    /**
     * 出错时
     */
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
     * 获取会话
     */
    @Override
    public Session getSession() {
        return sessionShell;
    }


    /**
     * 连接
     */
    public void connect() throws IOException {
        if (isConnecting.get()) {
            return;
        } else {
            isConnecting.set(true);
        }

        try {
            if (real != null) {
                real.close(Constants.CLOSE2002_RECONNECT);
            }

            real = connector.connect();
            //原始 session 切换为带壳的 session
            real.setSession(sessionShell);
            //同步握手信息
            this.setHandshake(real.getHandshake());
        } finally {
            isConnecting.set(false);
        }
    }

    private void internalCloseIfError() {
        if (real != null) {
            real.close(Constants.CLOSE2001_ERROR);
            real = null;
        }
    }


    /**
     * 预备检测
     *
     * @return 是否为新链接
     */
    private boolean internalCheck() throws IOException {
        if (real == null || real.isValid() == false) {
            this.connect();

            return true;
        } else {
            return false;
        }
    }
}