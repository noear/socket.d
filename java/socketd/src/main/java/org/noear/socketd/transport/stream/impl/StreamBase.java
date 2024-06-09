package org.noear.socketd.transport.stream.impl;

import org.noear.socketd.exception.SocketDTimeoutException;
import org.noear.socketd.transport.stream.Stream;
import org.noear.socketd.transport.stream.StreamInternal;
import org.noear.socketd.transport.stream.StreamManger;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * 流基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class StreamBase<T extends Stream> implements StreamInternal<T> {
    private static final Logger log = LoggerFactory.getLogger(Stream.class);

    //保险任务
    private ScheduledFuture<?> insuranceFuture;

    private final String sid;
    private final int demands;

    private long timeout;
    protected Consumer<Throwable> doOnError;
    protected TriConsumer<Boolean, Integer, Integer> doOnProgress;

    public StreamBase(String sid, int demands, long timeout) {
        this.sid = sid;
        this.demands = demands;
        this.timeout = timeout;
    }

    @Override
    public String sid() {
        return sid;
    }

    /**
     * 需求数量（0，1，2）
     */
    @Override
    public int demands() {
        return demands;
    }

    /**
     * 配置超时
     */
    public T timeout(long timeout){
        this.timeout = timeout;
        return (T)this;
    }

    /**
     * 超时
     */
    @Override
    public long timeout() {
        return timeout;
    }

    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     *
     * @param streamManger  流管理器
     * @param streamTimeout 流超时
     */
    @Override
    public void insuranceStart(StreamManger streamManger, long streamTimeout) {
        if (insuranceFuture != null) {
            return;
        }

        insuranceFuture = RunUtils.delay(() -> {
            streamManger.removeStream(sid);
            this.onError(new SocketDTimeoutException("The stream response timeout, sid=" + sid));
        }, streamTimeout);
    }

    /**
     * 保险取消息
     */
    @Override
    public void insuranceCancel() {
        if (insuranceFuture != null) {
            insuranceFuture.cancel(false);
        }
    }

    @Override
    public void onError(Throwable error) {
        if (doOnError != null) {
            doOnError.accept(error);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("The stream error, sid={}", sid(), error);
            }
        }
    }

    @Override
    public void onProgress(boolean isSend, int val, int max) {
        if (doOnProgress != null) {
            doOnProgress.accept(isSend, val, max);
        }
    }

    @Override
    public T thenError(Consumer<Throwable> onError) {
        this.doOnError = onError;
        return (T) this;
    }

    @Override
    public T thenProgress(TriConsumer<Boolean, Integer, Integer> onProgress) {
        this.doOnProgress = onProgress;
        return (T) this;
    }
}
