/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.utils {
    /**
     * @author noear
     * @since 2.0
     * @class
     */
    export class RunUtils {
        static __static_initialized: boolean = false;
        static __static_initialize() { if (!RunUtils.__static_initialized) { RunUtils.__static_initialized = true; RunUtils.__static_initializer_0(); } }

        /**
         * 异步执行器（一般用于执行 @Async 注解任务）
         */
        static asyncExecutor: java.util.concurrent.ExecutorService; public static asyncExecutor_$LI$(): java.util.concurrent.ExecutorService { RunUtils.__static_initialize();  return RunUtils.asyncExecutor; }

        /**
         * 调度执行器（一般用于延时任务）
         */
        static scheduledExecutor: java.util.concurrent.ScheduledExecutorService; public static scheduledExecutor_$LI$(): java.util.concurrent.ScheduledExecutorService { RunUtils.__static_initialize();  return RunUtils.scheduledExecutor; }

        static  __static_initializer_0() {
            const asyncPoolSize: number = java.lang.Runtime.getRuntime().availableProcessors() * 2;
            RunUtils.asyncExecutor = new java.util.concurrent.ThreadPoolExecutor(asyncPoolSize, asyncPoolSize, 0, java.util.concurrent.TimeUnit.MILLISECONDS, <any>(new java.util.concurrent.LinkedBlockingQueue<() => void>()), new org.noear.socketd.utils.NamedThreadFactory("Socketd-asyncExecutor-"));
            RunUtils.scheduledExecutor = new java.util.concurrent.ScheduledThreadPoolExecutor(java.lang.Runtime.getRuntime().availableProcessors(), new org.noear.socketd.utils.NamedThreadFactory("Socketd-echeduledExecutor-"));
        }

        public static setScheduledExecutor(scheduledExecutor: java.util.concurrent.ScheduledExecutorService) {
            if (scheduledExecutor != null){
                const old: java.util.concurrent.ScheduledExecutorService = RunUtils.scheduledExecutor_$LI$();
                RunUtils.scheduledExecutor = scheduledExecutor;
                old.shutdown();
            }
        }

        public static setAsyncExecutor(asyncExecutor: java.util.concurrent.ExecutorService) {
            if (asyncExecutor != null){
                const old: java.util.concurrent.ExecutorService = RunUtils.asyncExecutor_$LI$();
                RunUtils.asyncExecutor = asyncExecutor;
                old.shutdown();
            }
        }

        /**
         * 运行或异常
         * @param {*} task
         */
        public static runOrThrow(task: org.noear.socketd.utils.RunnableEx) {
            try {
                task.run();
            } catch(e) {
                e = org.noear.socketd.utils.Utils.throwableUnwrap(e);
                if (e != null && (e["__classes"] && e["__classes"].indexOf("java.lang.RuntimeException") >= 0) || e != null && e instanceof <any>Error){
                    throw <Error>e;
                } else {
                    throw Object.defineProperty(new Error(e.message), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.Exception'] });
                }
            }
        }

        public static runAndTry(task: org.noear.socketd.utils.RunnableEx) {
            try {
                task.run();
            } catch(e) {
            }
        }

        public static async$java_lang_Runnable(task: () => void): java.util.concurrent.CompletableFuture<void> {
            return java.util.concurrent.CompletableFuture.runAsync(<any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return () =>  (funcInst['run'] ? funcInst['run'] : funcInst) .call(funcInst)})(task)), RunUtils.asyncExecutor_$LI$());
        }

        /**
         * 异步执行
         * @param {() => void} task
         * @return {java.util.concurrent.CompletableFuture}
         */
        public static async<T0 = any>(task?: any): any {
            if (((typeof task === 'function' && (<any>task).length === 0) || task === null)) {
                return <any>org.noear.socketd.utils.RunUtils.async$java_lang_Runnable(task);
            } else if (((typeof task === 'function' && (<any>task).length === 0) || task === null)) {
                return <any>org.noear.socketd.utils.RunUtils.async$java_util_function_Supplier(task);
            } else throw new Error('invalid overload');
        }

        public static async$java_util_function_Supplier<U>(task: () => U): java.util.concurrent.CompletableFuture<U> {
            return java.util.concurrent.CompletableFuture.supplyAsync<any>(<any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return () =>  (funcInst['get'] ? funcInst['get'] : funcInst) .call(funcInst)})(task)), RunUtils.asyncExecutor_$LI$());
        }

        /**
         * 尝试异步执行
         * @param {*} task
         * @return {java.util.concurrent.CompletableFuture}
         */
        public static asyncAndTry(task: org.noear.socketd.utils.RunnableEx): java.util.concurrent.CompletableFuture<void> {
            return java.util.concurrent.CompletableFuture.runAsync(() => {
                RunUtils.runAndTry(task);
            }, RunUtils.asyncExecutor_$LI$());
        }

        /**
         * 延迟执行
         * @param {() => void} task
         * @param {number} millis
         * @return {*}
         */
        public static delay(task: () => void, millis: number): java.util.concurrent.ScheduledFuture<any> {
            return RunUtils.scheduledExecutor_$LI$().schedule(<any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return () =>  (funcInst['run'] ? funcInst['run'] : funcInst) .call(funcInst)})(task)), millis, java.util.concurrent.TimeUnit.MILLISECONDS);
        }

        /**
         * 延迟执行并重复
         * @param {() => void} task
         * @param {number} millis
         * @return {*}
         */
        public static delayAndRepeat(task: () => void, millis: number): java.util.concurrent.ScheduledFuture<any> {
            return RunUtils.scheduledExecutor_$LI$().scheduleWithFixedDelay(<any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return () =>  (funcInst['run'] ? funcInst['run'] : funcInst) .call(funcInst)})(task)), 1000, millis, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }
    RunUtils["__class"] = "org.noear.socketd.utils.RunUtils";

}


org.noear.socketd.utils.RunUtils.__static_initialize();
