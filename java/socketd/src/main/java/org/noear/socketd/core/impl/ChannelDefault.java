package org.noear.socketd.core.impl;

import org.noear.socketd.core.*;
import org.noear.socketd.utils.RangeUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 通道默认实现（每个连接都会建立一个通道）
 *
 * @author noear
 * @since 2.0
 */
public class ChannelDefault<S> extends ChannelBase implements Channel {
    private final S source;

    //接收器注册
    private final Map<String, Acceptor> acceptorMap;
    //助理
    private final ChannelAssistant<S> assistant;
    //会话（懒加载）
    private Session session;

    public ChannelDefault(S source, Config config,  ChannelAssistant<S> assistant) {
        super(config);
        this.source = source;
        this.assistant = assistant;
        this.acceptorMap = new HashMap<>();
    }

    @Override
    public boolean isValid() {
        return assistant.isValid(source);
    }

    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return assistant.getRemoteAddress(source);
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return assistant.getLocalAddress(source);
    }

    /**
     * 发送
     */
    @Override
    public void send(Frame frame, Acceptor acceptor) throws IOException {
        if (frame.getMessage() != null) {
            Message message = frame.getMessage();

            //注册接收器
            if (acceptor != null) {
                acceptorMap.put(message.getKey(), acceptor);
            }

            //尝试分片
            if (message.getEntity() != null) {
                if (message.getEntity().getDataSize() > getConfig().getMaxRangeSize()) {
                    AtomicReference<Integer> rangeIndex = new AtomicReference<>(0);
                    while (true) {
                        Entity rangeEntity = RangeUtils.nextRange(getConfig(), rangeIndex, message.getEntity());

                        if (rangeEntity != null) {
                            //主要是 key 和 entity
                            Frame rangeFrame = new Frame(frame.getFlag(), new MessageDefault()
                                    .flag(frame.getFlag())
                                    .key(message.getKey())
                                    .entity(rangeEntity));

                            assistant.write(source, rangeFrame);
                        } else {
                            return;
                        }
                    }
                }
            }
        }

        assistant.write(source, frame);
    }

    /**
     * 收回（收回答复）
     * */
    @Override
    public void retrieve(Frame frame) throws IOException {
        Acceptor acceptor = acceptorMap.get(frame.getMessage().getKey());

        if (acceptor != null) {
            if (acceptor.isSingle() || frame.getFlag() == Flag.ReplyEnd) {
                acceptorMap.remove(frame.getMessage().getKey());
            }

            acceptor.accept(frame.getMessage());
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
        assistant.close(source);
        acceptorMap.clear();
    }
}