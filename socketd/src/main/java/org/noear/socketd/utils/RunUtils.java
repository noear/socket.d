package org.noear.socketd.utils;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 2.0
 */
public class RunUtils {
    /**
     * 异步执行器（一般用于执行 @Async 注解任务）
     */
    private static ExecutorService asyncExecutor;
    /**
     * 调度执行器（一般用于延时任务）
     */
    private static ScheduledExecutorService scheduledExecutor;

    static {
        int asyncPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        asyncExecutor = new ThreadPoolExecutor(asyncPoolSize, asyncPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory("Solon-asyncExecutor-"));

        scheduledExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                new NamedThreadFactory("Solon-echeduledExecutor-"));
    }

    public static void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        if (scheduledExecutor != null) {
            ScheduledExecutorService old = RunUtils.scheduledExecutor;
            RunUtils.scheduledExecutor = scheduledExecutor;
            old.shutdown();
        }
    }


    public static void setAsyncExecutor(ExecutorService asyncExecutor) {
        if (asyncExecutor != null) {
            ExecutorService old = RunUtils.asyncExecutor;
            RunUtils.asyncExecutor = asyncExecutor;
            old.shutdown();
        }
    }

    /**
     * 运行或异常
     */
    public static void runOrThrow(RunnableEx task) {
        try {
            task.run();
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static void runAnTry(RunnableEx task){
        try {
            task.run();
        } catch (Throwable e) {
            //略...
        }
    }


    /**
     * 异步执行
     */
    public static CompletableFuture<Void> async(Runnable task) {
        return CompletableFuture.runAsync(task, asyncExecutor);
    }

    /**
     * 异步执行
     */
    public static <U> CompletableFuture<U> async(Supplier<U> task) {
        return CompletableFuture.supplyAsync(task, asyncExecutor);
    }

    /**
     * 延迟执行
     */
    public static ScheduledFuture<?> delay(Runnable task, long millis) {
        return scheduledExecutor.schedule(task, millis, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟执行并重复
     */
    public static ScheduledFuture<?> delayAndRepeat(Runnable task, long millis) {
        return scheduledExecutor.scheduleWithFixedDelay(task, 1000, millis, TimeUnit.MILLISECONDS);
    }
}
