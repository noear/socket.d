package org.noear.socketd.transport.client;

import org.noear.socketd.exception.SocketdChannelException;
import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.internal.HeartbeatHandlerDefault;
import org.noear.socketd.utils.RunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * 客户端通道
 *
 * @author noear
 * @since 2.0
 */
public class ClientChannel extends ChannelBase implements Channel {
    private static final Logger log = LoggerFactory.getLogger(ClientChannel.class);

    //连接器
    private ClientConnector connector;
    //真实通道
    private Channel real;
    //心跳处理
    private HeartbeatHandler heartbeatHandler;
    //心跳调度
    private ScheduledFuture<?> heartbeatScheduledFuture;

    public ClientChannel(Channel real, ClientConnector connector) {
        super(real.getConfig());
        this.real = real;
        this.connector = connector;
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
        if (connector.autoReconnect()) {
            if (heartbeatScheduledFuture == null || heartbeatScheduledFuture.isCancelled()) {
                heartbeatScheduledFuture = RunUtils.delayAndRepeat(() -> {
                    try {
                        heartbeatHandle();
                    } catch (Exception e) {
                        if (log.isDebugEnabled()) {
                            log.debug("{}", e);
                        }
                    }
                }, connector.heartbeatInterval());
            }
        }
    }


    /**
     * 移除接收器（答复接收器）
     */
    @Override
    public void removeAcceptor(String sid) {
        if (real != null) {
            real.removeAcceptor(sid);
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
    public boolean isClosed() {
        if (real == null) {
            return false;
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
            if(real.getHandshake() == null) {
                return;
            }

            //手明手动关闭或被控制性关闭
            if (real.isClosed()) {
                if (log.isDebugEnabled()) {
                    log.debug("The channel is closed, sessionId={}", getSession().sessionId());
                }
                return;
            }
        }

        synchronized (this) {
            try {
                prepareCheck();

                heartbeatHandler.heartbeat(getSession());
            } catch (SocketdException e) {
                throw e;
            } catch (Throwable e) {
                if (connector.autoReconnect()) {
                    real.close();
                    real = null;
                }

                throw new SocketdChannelException(e);
            }
        }
    }

    /**
     * 发送
     *
     * @param frame    帧
     * @param acceptor 答复接收器（没有则为 null）
     */
    @Override
    public void send(Frame frame, Acceptor acceptor) throws IOException {
        Asserts.assertClosed(real);

        synchronized (this) {
            try {
                prepareCheck();

                real.send(frame, acceptor);
            } catch (SocketdException e) {
                throw e;
            } catch (Throwable e) {
                if (connector.autoReconnect()) {
                    real.close();
                    real = null;
                }
                throw new SocketdChannelException(e);
            }
        }
    }

    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    @Override
    public void retrieve(Frame frame, Consumer<Throwable> onError) {
        real.retrieve(frame, onError);
    }

    /**
     * 获取会话
     */
    @Override
    public Session getSession() {
        return real.getSession();
    }


    @Override
    public void reconnect() throws Exception {
        initHeartbeat();

        prepareCheck();
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        RunUtils.runAnTry(() -> heartbeatScheduledFuture.cancel(true));
        RunUtils.runAnTry(() -> connector.close());
        RunUtils.runAnTry(() -> real.close());
    }


    /**
     * 预备检
     *
     * @return 是否为新链接
     */
    private boolean prepareCheck() throws Exception {
        if (real == null || real.isValid() == false) {
            real = connector.connect();

            return true;
        } else {
            return false;
        }
    }
}