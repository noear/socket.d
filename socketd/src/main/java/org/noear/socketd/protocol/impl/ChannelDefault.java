package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.*;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class ChannelDefault<S> extends ChannelBase implements Channel {
    private S source;
    private Closeable sourceCloseable;
    private OutputTarget<S> outputTarget;
    private Session session;

    public ChannelDefault(S source, Closeable sourceCloseable, OutputTarget<S> outputTarget) {
        super();
        this.source = source;
        this.sourceCloseable = sourceCloseable;
        this.outputTarget = outputTarget;
    }

    @Override
    public void send(Frame frame) throws IOException {
        outputTarget.write(source, frame);
    }

    @Override
    public Session getSession() {
        if (session == null) {
            session = new SessionDefault(this);
        }

        return session;
    }

    @Override
    public void close() throws IOException {
        sourceCloseable.close();
    }
}
