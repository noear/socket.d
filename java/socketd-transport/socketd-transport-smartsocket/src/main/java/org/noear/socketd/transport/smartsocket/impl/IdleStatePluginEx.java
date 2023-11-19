package org.noear.socketd.transport.smartsocket.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.channels.AsynchronousSocketChannelProxy;
import org.smartboot.socket.extension.plugins.AbstractPlugin;
import org.smartboot.socket.extension.plugins.IdleStatePlugin;
import org.smartboot.socket.timer.HashedWheelTimer;
import org.smartboot.socket.timer.TimerTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 2.0
 */
public class IdleStatePluginEx <T> extends AbstractPlugin<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdleStatePlugin.class);
    private static final HashedWheelTimer timer = new HashedWheelTimer((r) -> {
        Thread thread = new Thread(r, "idleStateMonitor");
        thread.setDaemon(true);
        return thread;
    });
    private final int idleTimeout;
    private final boolean writeMonitor;
    private final boolean readMonitor;

    public IdleStatePluginEx(int idleTimeout) {
        this(idleTimeout, true, true);
    }

    public IdleStatePluginEx(int idleTimeout, boolean readMonitor, boolean writeMonitor) {
        if (idleTimeout <= 0) {
            throw new IllegalArgumentException("invalid idleTimeout");
        } else if (!writeMonitor && !readMonitor) {
            throw new IllegalArgumentException("readIdle and writeIdle both disable");
        } else {
            this.idleTimeout = idleTimeout;
            this.writeMonitor = writeMonitor;
            this.readMonitor = readMonitor;
        }
    }

    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        return new IdleMonitorChannel(channel);
    }

    public class IdleMonitorChannel extends AsynchronousSocketChannelProxy {
        TimerTask task;
        long readTimestamp;
        long writeTimestamp;

        public IdleMonitorChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
            super(asynchronousSocketChannel);
            if (!IdleStatePluginEx.this.readMonitor) {
                this.readTimestamp = Long.MAX_VALUE;
            }

            if (!IdleStatePluginEx.this.writeMonitor) {
                this.writeTimestamp = Long.MAX_VALUE;
            }

            this.task = IdleStatePluginEx.timer.scheduleWithFixedDelay(() -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - this.readTimestamp > (long) IdleStatePluginEx.this.idleTimeout || currentTime - this.writeTimestamp > (long) IdleStatePluginEx.this.idleTimeout) {
                    try {
                        if (asynchronousSocketChannel.isOpen()) {
                            if (IdleStatePluginEx.LOGGER.isDebugEnabled()) {
                                IdleStatePluginEx.LOGGER.debug("Channel idle timeout, remoteIp={}", asynchronousSocketChannel.getRemoteAddress());
                            }
                        }

                        this.close();
                    } catch (IOException var5) {
                        IdleStatePluginEx.LOGGER.debug("close exception", var5);
                    }
                }

            }, (long) IdleStatePluginEx.this.idleTimeout, TimeUnit.MILLISECONDS);
        }

        public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
            if (IdleStatePluginEx.this.readMonitor) {
                this.readTimestamp = System.currentTimeMillis();
            }

            super.read(dst, timeout, unit, attachment, handler);
        }

        public <A> void write(ByteBuffer src, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
            if (IdleStatePluginEx.this.writeMonitor) {
                this.writeTimestamp = System.currentTimeMillis();
            }

            super.write(src, timeout, unit, attachment, handler);
        }

        public void close() throws IOException {
            this.task.cancel();
            super.close();
        }
    }
}