package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 通道默认实现
 *
 * @author noear
 * @since 2.0
 */
public class ChannelDefault<S> extends ChannelBase implements Channel {
    private S source;
    private Closeable sourceCloseable;
    private OutputTarget<S> outputTarget;
    private Session session;
    private Map<String, Acceptor> acceptorMap;

    public ChannelDefault(S source, Closeable sourceCloseable, OutputTarget<S> outputTarget) {
        super();
        this.source = source;
        this.sourceCloseable = sourceCloseable;
        this.outputTarget = outputTarget;
        this.acceptorMap = new HashMap<>();
    }

    /**
     * 发送
     */
    @Override
    public void send(Frame frame, Acceptor acceptor) throws IOException {
        if (acceptor != null) {
            acceptorMap.put(frame.getPayload().getKey(), acceptor);
        }

        outputTarget.write(source, frame);
    }

    @Override
    public void retrieve(Frame frame) throws IOException {
        Acceptor acceptor = acceptorMap.get(frame.getPayload().getKey());

        if (acceptor != null) {
            if (acceptor.isSingle()) {
                acceptorMap.remove(frame.getPayload().getKey());
            }
            acceptor.accept(frame.getPayload());
        }
    }

    /**
     * 获取会话
     */
    @Override
    public Session getSession() {
        if (session == null) {
            session = new SessionDefault(this);
        }

        return session;
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        sourceCloseable.close();
        acceptorMap.clear();
    }
}