package org.noear.socketd.utils;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 2.0
 */
public class RunUtils {
    /**
     * 异步执行器（一般用于异步任务）
     */
    private static ExecutorService singleExecutor;
    /**
     * 异步执行器（一般用于异步任务）
     */
    private static ExecutorService asyncExecutor;
    /**
     * 调度执行器（一般用于延时任务）
     */
    private static ScheduledExecutorService scheduledExecutor;

    static {
        singleExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("Socketd-singleExecutor-"));

        int asyncPoolSize = Math.max(Runtime.getRuntime().availableProcessors(), 2);
        asyncExecutor = new ThreadPoolExecutor(asyncPoolSize, asyncPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory("Socketd-asyncExecutor-"));

        int scheduledPoolSize = 2;
        scheduledExecutor = new ScheduledThreadPoolExecutor(scheduledPoolSize,
                new NamedThreadFactory("Socketd-scheduledExecutor-"));
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

    public static void runAndTry(RunnableEx task) {
        try {
            task.run();
        } catch (Throwable e) {
            //略...
        }
    }

    /**
     * 异步执行（单线程）
     */
    public static CompletableFuture<Void> single(Runnable task){
        return CompletableFuture.runAsync(task, singleExecutor);
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
     * 尝试异步执行
     */
    public static CompletableFuture<Void> asyncAndTry(RunnableEx task) {
        return CompletableFuture.runAsync(() -> {
            runAndTry(task);
        }, asyncExecutor);
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

    /**
     * 定时任务
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long millisPeriod) {
        return scheduledExecutor.scheduleAtFixedRate(task, initialDelay, millisPeriod, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时任务
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long millisDelay) {
        return scheduledExecutor.scheduleWithFixedDelay(task, initialDelay, millisDelay, TimeUnit.MILLISECONDS);
    }
}