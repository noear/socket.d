package org.noear.socketd.transport.core;

import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.utils.RunUtils;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * 流基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class StreamBase implements StreamInternal {
    //保险任务
    private ScheduledFuture<?> insuranceFuture;

    private final String sid;
    private final boolean isSingle;
    private final long timeout;
    private Consumer<Throwable> doOnError;

    public StreamBase(String sid, boolean isSingle, long timeout) {
        this.sid = sid;
        this.isSingle = isSingle;
        this.timeout = timeout;
    }

    @Override
    public String sid() {
        return sid;
    }

    /**
     * 是否单发接收
     */
    @Override
    public boolean isSingle() {
        return isSingle;
    }

    /**
     * 超时
     * */
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
            this.onError(new SocketdTimeoutException("The stream response timeout, sid=" + sid));
        }, streamTimeout);
    }

    /**
     * 保险取消息
     * */
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
        }
    }

    @Override
    public Stream thenError(Consumer<Throwable> onError) {
        this.doOnError = onError;
        return this;
    }
}
