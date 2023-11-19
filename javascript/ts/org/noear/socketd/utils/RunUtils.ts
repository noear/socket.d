import {RunnableEx} from "./Functions";

/**
 * @author noear
 * @since 2.0
 * @class
 */
export class RunUtils {

    /**
     * 运行或异常
     * @param {*} task
     */
    public static runOrThrow(task: RunnableEx) {
        task();
    }

    public static runAndTry(task: RunnableEx) {
        try {
            task();
        } catch(e) {
        }
    }

    public static async(task: RunnableEx) : void{

    }

    /**
     * 尝试异步执行
     * @param {*} task
     * @return {java.util.concurrent.CompletableFuture}
     */
    public static asyncAndTry(task: RunnableEx): void {

    }

    /**
     * 延迟执行
     * @param {() => void} task
     * @param {number} millis
     * @return {*}
     */
    public static delay(task:TimerHandler, millis: number): number{
      return   setTimeout(task, millis);
    }

    /**
     * 延迟执行并重复
     * @param {() => void} task
     * @param {number} millis
     * @return {*}
     */
    public static delayAndRepeat(task: TimerHandler, millis: number):number{
      return   setInterval(task, millis);
    }
}