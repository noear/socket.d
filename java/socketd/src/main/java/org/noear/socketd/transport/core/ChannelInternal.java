package org.noear.socketd.transport.core;

import org.noear.socketd.transport.stream.StreamInternal;

import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;

/**
 * 通道内部接口
 *
 * @author noear
 * @since 2.0
 */
public interface ChannelInternal extends Channel {
    /**
     * 设置会话
     */
    void setSession(Session session);

    /**
     * 更新最后活动时间
     */
    void setLiveTimeAsNow();

    /**
     * 获取流
     */
    StreamInternal getStream(String sid);

    /**
     * 当打开时
     */
    void onOpenFuture(BiConsumer<Boolean, Throwable> future);

    /**
     * 执行打开时
     */
    void doOpenFuture(boolean isOk, Throwable error);

    /**
     * 写申请
     */
    default void writeAcquire(Frame frame) {
        if (frame.flag() < Flags.Message) {
            return;
        }

        Semaphore tmp = getConfig().getWriteSemaphore();
        if (tmp != null) {
            tmp.acquireUninterruptibly();
        }
    }

    /**
     * 写释放
     */
    default void writeRelease(Frame frame) {
        if (frame.flag() < Flags.Message) {
            return;
        }

        Semaphore tmp = getConfig().getWriteSemaphore();
        if (tmp != null) {
            tmp.release();
        }
    }


    /**
     * 写申请
     */
    default void readAcquire(Frame frame) {
        if (frame.flag() < Flags.Message) {
            return;
        }

        Semaphore tmp = getConfig().getReadSemaphore();
        if (tmp != null) {
            tmp.acquireUninterruptibly();
        }
    }

    /**
     * 写释放
     */
    default void readRelease(Frame frame) {
        if (frame.flag() < Flags.Message) {
            return;
        }

        Semaphore tmp = getConfig().getReadSemaphore();
        if (tmp != null) {
            tmp.release();
        }
    }
}