package org.noear.socketd.client;

import org.noear.socketd.protocol.*;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author noear
 * @since 2.0
 */
public class ClientChannel extends ChannelBase implements Channel {
    private Connector connector;
    private Channel real;

    public ClientChannel(Channel real, Connector connector) {
        this.real = real;
        this.connector = connector;
    }

    /**
     * @return 是否为新链接
     */
    private boolean prepareSend() throws IOException {
        if (real == null) {
            real = connector.connect();
            //onOpen();

            return true;
        } else {
            return false;
        }
    }

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

    @Override
    public Session getSession() {
        return real.getSession();
    }

    @Override
    public void close() throws IOException {
        real.close();
    }
}
