/**
 * 运行工具
 *
 * @author noear
 * @since 2.0
 */
export class RunUtils {
    static runAndTry(fun) {
        try {
            fun();
        } catch (e) {

        }
    }
}