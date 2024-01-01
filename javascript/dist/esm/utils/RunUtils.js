export class RunUtils {
    static runAndTry(fun) {
        try {
            fun();
        }
        catch (e) {
        }
    }
}
