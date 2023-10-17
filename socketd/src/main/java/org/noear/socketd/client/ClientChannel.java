package org.noear.socketd.client;

import org.noear.socketd.protocol.*;
import org.noear.socketd.protocol.impl.HeartbeatHandlerDefault;
import org.noear.socketd.utils.RunUtils;

import java.io.IOException;
import java.net.SocketException;

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

    public ClientChannel(Channel real, ClientConnector connector) {
        this.real = real;
        this.connector = connector;
        this.heartbeatHandler = connector.heartbeatHandler();

        if (heartbeatHandler == null) {
            heartbeatHandler = new HeartbeatHandlerDefault();
        }

        RunUtils.delayAndRepeat(() -> {
            heartbeatHandle();
        }, connector.getHeartbeatInterval());
    }

    /**
     * 发送
     */
    @Override
    public void send(Frame frame) throws IOException {
        synchronized (this) {
            try {
                prepareSend();

                real.send(frame);
            } catch (SocketException e) {
                if (connector.autoReconnect()) {
                    real = null;
                }

                throw new RuntimeException(e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
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
        real.close();
    }

    /**
     * 心跳处理
     */
    private void heartbeatHandle() {
        synchronized (this) {
            try {
                prepareSend();

                heartbeatHandler.heartbeatHandle(getSession());
            } catch (SocketException e) {
                if (connector.autoReconnect()) {
                    real = null;
                }

                throw new RuntimeException(e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }



    /**
     * 预备发送
     *
     * @return 是否为新链接
     */
    private boolean prepareSend() throws IOException {
        if (real == null) {
            real = connector.connect();

            return true;
        } else {
            return false;
        }
    }
}
