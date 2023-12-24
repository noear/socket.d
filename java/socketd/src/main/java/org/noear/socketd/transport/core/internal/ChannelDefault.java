package org.noear.socketd.transport.core.internal;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.StreamBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

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
    //打开前景（用于构建 onOpen 异步处理）
    private final CompletableFuture<Boolean> onOpenFuture = new CompletableFuture<>();

    public ChannelDefault(S source, ChannelSupporter<S> supporter) {
        super(supporter.config());
        this.source = source;
        this.processor = supporter.processor();
        this.assistant = supporter.assistant();
        this.streamManger = supporter.config().getStreamManger();
    }

    /**
     * 是否有效
     */
    @Override
    public boolean isValid() {
        return isClosed() == 0 && assistant.isValid(source);
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
    public void send(Frame frame, StreamBase stream) throws IOException {
        Asserts.assertClosed(this);

        if (log.isDebugEnabled()) {
            if (getConfig().clientMode()) {
                log.debug("C-SEN:{}", frame);
            } else {
                log.debug("S-SEN:{}", frame);
            }
        }

        synchronized (SEND_LOCK) {
            if (frame.getMessage() != null) {
                MessageInternal message = frame.getMessage();

                //注册流接收器
                if (stream != null) {
                    streamManger.addStream(message.sid(), stream);
                }

                //如果有实体（尝试分片）
                if (message.entity() != null) {
                    //确保用完自动关闭

                    if (message.dataSize() > getConfig().getFragmentSize()) {
                        message.metaMap().put(EntityMetas.META_DATA_LENGTH, String.valueOf(message.dataSize()));

                        //满足分片条件
                        int fragmentIndex = 0;
                        while (true) {
                            //获取分片
                            fragmentIndex++;
                            Entity fragmentEntity = getConfig().getFragmentHandler().nextFragment(this, fragmentIndex, message);

                            if (fragmentEntity != null) {
                                //主要是 sid 和 entity
                                Frame fragmentFrame = new Frame(frame.getFlag(), new MessageDefault()
                                        .flag(frame.getFlag())
                                        .sid(message.sid())
                                        .entity(fragmentEntity));

                                assistant.write(source, fragmentFrame);
                            } else {
                                //没有分片，说明发完了
                                return;
                            }
                        }
                    } else {
                        //不满足分片条件，直接发
                        assistant.write(source, frame);
                        return;
                    }

                }
            }

            assistant.write(source, frame);
        }
    }

    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    @Override
    public void retrieve(Frame frame) {
        final StreamInternal stream = streamManger.getStream(frame.getMessage().sid());

        if (stream != null) {
            if (stream.isSingle() || frame.getFlag() == Flags.ReplyEnd) {
                //如果是单收或者答复结束，则移除流接收器
                streamManger.removeStream(frame.getMessage().sid());
            }

            if (stream.isSingle()) {
                //单收时，内部已经是异步机制
                stream.onAccept(frame.getMessage(), this);
            } else {
                //改为异步处理，避免卡死Io线程
                getConfig().getChannelExecutor().submit(() -> {
                    stream.onAccept(frame.getMessage(), this);
                });
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("{} stream not found, sid={}, sessionId={}",
                        getConfig().getRoleName(), frame.getMessage().sid(), getSession().sessionId());
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
    public CompletableFuture<Boolean> onOpenFuture() {
        return onOpenFuture;
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