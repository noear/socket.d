import {ClientSession} from "../transport/client/ClientSession";

/**
 * 会话工具（主要检测状态）
 *
 * @author noear
 * @since 2.5
 */
export class SessionUtils {
    /**
     * 是否活动的
     */
    static isActive(s: ClientSession | null): boolean {
        return s != null && s.isActive();
    }

    /**
     * 是否有效的
     */
    static isValid(s: ClientSession | null): boolean {
        return s != null && s.isValid();
    }
}