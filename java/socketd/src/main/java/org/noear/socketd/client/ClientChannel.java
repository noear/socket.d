package org.noear.socketd.client;

import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.core.*;
import org.noear.socketd.core.impl.HeartbeatHandlerDefault;
import org.noear.socketd.utils.RunUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;

/**
 * 客户端通道
 *
 * @author noear
 * @since 2.0
 */
public class ClientChannel extends ChannelBase implements Channel {
    private ClientConnector connector;
    private Channel real;
    private HeartbeatHandler heartbeatHandler;
    private ScheduledFuture<?> scheduledFuture;

    public ClientChannel(Channel real, ClientConnector connector) {
        super(real.getConfig());
        this.real = real;
        this.connector = connector;
        this.heartbeatHandler = connector.heartbeatHandler();

        if (heartbeatHandler == null) {
            heartbeatHandler = new HeartbeatHandlerDefault();
        }

        if (connector.autoReconnect() && scheduledFuture == null) {
            scheduledFuture = RunUtils.delayAndRepeat(() -> {
                heartbeatHandle();
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
     * 发送
     */
    @Override
    public void send(Frame frame, Acceptor acceptor) throws IOException {
        synchronized (this) {
            try {
                prepareSend();

                real.send(frame, acceptor);
            } catch (Throwable e) {
                if (connector.autoReconnect()) {
                    real = null;
                }

                throw new SocketdConnectionException(e);
            }
        }
    }

    /**
     * 收回（收回答复帧）
     *
     * @param frame 帧
     */
    @Override
    public void retrieve(Frame frame) throws IOException {
        real.retrieve(frame);
    }

    /**
     * 获取会话
     */
    @Override
    public Session getSession() {
        return real.getSession();
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        RunUtils.runAnTry(() -> scheduledFuture.cancel(true));
        RunUtils.runAnTry(() -> connector.close());
        RunUtils.runAnTry(() -> real.close());
    }

    /**
     * 心跳处理
     */
    private void heartbeatHandle() {
        synchronized (this) {
            try {
                prepareSend();

                heartbeatHandler.heartbeatHandle(getSession());
            } catch (Throwable e) {
                if (connector.autoReconnect()) {
                    real = null;
                }

                throw new SocketdConnectionException(e);
            }
        }
    }


    /**
     * 预备发送
     *
     * @return 是否为新链接
     */
    private boolean prepareSend() throws Exception {
        if (real == null) {
            real = connector.connect();

            return true;
        } else {
            return false;
        }
    }
}
