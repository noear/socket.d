package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.MessageBuilder;
import org.noear.socketd.transport.stream.StreamInternal;
import org.noear.socketd.transport.stream.StreamManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
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
    //发送锁
    private final ReentrantLock sendInFairLock;
    private final ReentrantLock sendNoFairLock;

    //会话（懒加载）
    private Session session;
    //最后活动时间
    private long liveTime;
    //打开前景（用于构建 onOpen 异步处理）
    private BiConsumer<Boolean, Throwable> onOpenFuture;
    //关闭代号（用于做关闭异常提醒）//可能协议关；可能用户关
    private int closeCode;
    //告警代号
    private int alarmCode;

    public ChannelDefault(S source, ChannelSupporter<S> supporter) {
        super(supporter.getConfig());
        this.source = source;
        this.processor = supporter.getProcessor();
        this.assistant = supporter.getAssistant();
        this.streamManger = supporter.getConfig().getStreamManger();
        this.sendInFairLock = new ReentrantLock(true);
        this.sendNoFairLock = new ReentrantLock(false);
    }

    /**
     * 是否有效
     */
    @Override
    public boolean isValid() {
        return closeCode() == 0 && assistant.isValid(source);
    }

    @Override
    public boolean isClosing() {
        return closeCode == Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING;
    }

    @Override
    public int closeCode() {
        if (closeCode > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            return closeCode;
        } else {
            return 0;
        }
    }

    @Override
    public long getLiveTime() {
        return liveTime;
    }

    @Override
    public void setLiveTimeAsNow() {
        liveTime = System.currentTimeMillis();
    }

    @Override
    public void setAlarmCode(int alarmCode) {
        this.alarmCode = alarmCode;
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

        //是否串行发送
        boolean isSerialSend = getConfig().isSerialSend();

        if (isSerialSend) {
            //公平锁
            sendInFairLock.lock();
            try {
                sendDo(frame, stream);
            } finally {
                sendInFairLock.unlock();
            }
        } else {
            //非公平锁
            sendNoFairLock.lock();
            try {
                sendDo(frame, stream);
            } finally {
                sendNoFairLock.unlock();
            }
        }
    }

    private void sendDo(Frame frame, StreamInternal stream) throws IOException {
        if(alarmCode == Constants.ALARM3001_PRESSURE) {
            if (frame.flag() >= Flags.Message && frame.flag() <= Flags.Subscribe) {
                if (frame.message().meta(EntityMetas.META_X_UNLIMITED) == null) {
                    try {
                        if (log.isDebugEnabled()) {
                            log.debug("Too much pressure, sleep=100ms");
                        }

                        setAlarmCode(0);
                        Thread.sleep(100);
                    } catch (Throwable ex) {
                        //略过
                    }
                }
            }
        }


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

                    processor.sendFrame(this, fragmentFrame, assistant, source);
                });
                return;
            }
        }

        //不满足分片条件，直接发
        processor.sendFrame(this, frame, assistant, source);
        if (stream != null) {
            stream.onProgress(true, 1, 1);
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
        try {
            this.closeCode = code;

            super.close(code);

            if (code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
                if (assistant.isValid(source)) {
                    //如果有效且非预关闭，则尝试关闭源
                    assistant.close(source);

                    if (log.isDebugEnabled()) {
                        log.debug("{} channel closed, sessionId={}", getConfig().getRoleName(), getSession().sessionId());
                    }
                }
            }
        } catch (Throwable e) {
            if (log.isWarnEnabled()) {
                log.warn("{} channel close error, sessionId={}",
                        getConfig().getRoleName(), getSession().sessionId(), e);
            }
        }

        if (code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            this.onCloseDo();
        }
    }


    private AtomicBoolean isCloseNotified = new AtomicBoolean(false);

    private void onCloseDo() {
        if (isCloseNotified.get() == false) {
            isCloseNotified.set(true);
            processor.doCloseNotice(this);
        }
    }
}