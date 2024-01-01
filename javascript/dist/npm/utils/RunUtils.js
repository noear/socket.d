"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.RunUtils = void 0;
class RunUtils {
    static runAndTry(fun) {
        try {
            fun();
        }
        catch (e) {
        }
    }
}
exports.RunUtils = RunUtils;
