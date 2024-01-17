package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.MessageBuilder;
import org.noear.socketd.transport.stream.StreamInternal;
import org.noear.socketd.transport.stream.StreamManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * 通道默认实现（每个连接都会建立一个或多个通道）
 *
 * @author noear
 * @since 2.0
 */
public class ChannelDefault<S> extends ChannelBase implements ChannelInternal {
    private static Logger log = LoggerFactory.getLogger(ChannelDefault.class);

    private final S source;

    //处理器
    private final Processor processor;
    //助理
    private final ChannelAssistant<S> assistant;
    //流管理器
    private final StreamManger streamManger;
    //会话（懒加载）
    private Session session;
    //最后活动时间
    private long liveTime;
    //打开前景（用于构建 onOpen 异步处理）
    private BiConsumer<Boolean, Throwable> onOpenFuture;

    public ChannelDefault(S source, ChannelSupporter<S> supporter) {
        super(supporter.getConfig());
        this.source = source;
        this.processor = supporter.getProcessor();
        this.assistant = supporter.getAssistant();
        this.streamManger = supporter.getConfig().getStreamManger();
    }

    /**
     * 是否有效
     */
    @Override
    public boolean isValid() {
        return isClosed() == 0 && assistant.isValid(source);
    }


    @Override
    public long getLiveTime() {
        return liveTime;
    }

    @Override
    public void setLiveTimeAsNow() {
        liveTime = System.currentTimeMillis();
    }

    /**
     * 获取远程地址
     */
    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return assistant.getRemoteAddress(source);
    }

    /**
     * 获取本地地址
     */
    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return assistant.getLocalAddress(source);
    }

    private Object SEND_LOCK = new Object();

    /**
     * 发送
     */
    @Override
    public void send(Frame frame, StreamInternal stream) throws IOException {
        Asserts.assertClosed(this);

        if (log.isDebugEnabled()) {
            if (getConfig().clientMode()) {
                log.debug("C-SEN:{}", frame);
            } else {
                log.debug("S-SEN:{}", frame);
            }
        }

        synchronized (SEND_LOCK) {
            if (frame.message() != null) {
                MessageInternal message = frame.message();

                //注册流接收器
                if (stream != null) {
                    streamManger.addStream(message.sid(), stream);
                }

                //如果有实体（尝试分片）
                if (message.entity() != null) {
                    //确保用完自动关闭

                    if (message.dataSize() > getConfig().getFragmentSize()) {
                        message.putMeta(EntityMetas.META_DATA_LENGTH, String.valueOf(message.dataSize()));
                    }

                    getConfig().getFragmentHandler().spliFragment(this, stream, message, fragmentEntity -> {
                        //主要是 sid 和 entity
                        Frame fragmentFrame;
                        if (fragmentEntity instanceof MessageInternal) {
                            fragmentFrame = new Frame(frame.flag(), (MessageInternal) fragmentEntity);
                        } else {
                            fragmentFrame = new Frame(frame.flag(), new MessageBuilder()
                                    .flag(frame.flag())
                                    .sid(message.sid())
                                    .event(message.event())
                                    .entity(fragmentEntity)
                                    .build());
                        }

                        assistant.write(source, fragmentFrame);
                    });
                    return;
                }
            }

            //不满足分片条件，直接发
            assistant.write(source, frame);
            if (stream != null) {
                stream.onProgress(true, 1, 1);
            }
        }
    }


    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    @Override
    public void retrieve(Frame frame, StreamInternal stream) {
        if (stream != null) {
            if (stream.demands() < Constants.DEMANDS_MULTIPLE || frame.flag() == Flags.ReplyEnd) {
                //如果是单收或者答复结束，则移除流接收器
                streamManger.removeStream(frame.message().sid());
            }

            if (stream.demands() < Constants.DEMANDS_MULTIPLE) {
                //单收时，内部已经是异步机制
                stream.onReply(frame.message());
            } else {
                //改为异步处理，避免卡死Io线程
                getConfig().getChannelExecutor().submit(() -> {
                    stream.onReply(frame.message());
                });
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("{} stream not found, sid={}, sessionId={}",
                        getConfig().getRoleName(), frame.message().sid(), getSession().sessionId());
            }
        }
    }

    /**
     * 手动重连（一般是自动）
     */
    @Override
    public void reconnect() throws IOException {
        //由 ClientChannel 实现
    }

    @Override
    public void onError(Throwable error) {
        processor.onError(this, error);
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

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public StreamInternal getStream(String sid) {
        return streamManger.getStream(sid);
    }

    @Override
    public void onOpenFuture(BiConsumer<Boolean, Throwable> future) {
        onOpenFuture = future;
    }

    @Override
    public void doOpenFuture(boolean isOk, Throwable error) {
        if (onOpenFuture != null) {
            onOpenFuture.accept(isOk, error);
        }
    }


    /**
     * 关闭
     */
    @Override
    public void close(int code) {
        if (log.isDebugEnabled()) {
            log.debug("{} channel will be closed, sessionId={}", getConfig().getRoleName(), getSession().sessionId());
        }

        try {
            super.close(code);
            assistant.close(source);
        } catch (Throwable e) {
            if (log.isWarnEnabled()) {
                log.warn("{} channel close error, sessionId={}",
                        getConfig().getRoleName(), getSession().sessionId(), e);
            }
        }
    }
}