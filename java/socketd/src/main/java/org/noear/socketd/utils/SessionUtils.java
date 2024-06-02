package org.noear.socketd.utils;

import org.noear.socketd.transport.client.ClientSession;

/**
 * 会话工具（主要检测状态）
 *
 * @author noear
 * @since 2.5
 */
public class SessionUtils {
    /**
     * 是否活动的
     */
    public static boolean isActive(ClientSession s) {
        return s != null && s.isActive();
    }

    /**
     * 是否有效的
     */
    public static boolean isValid(ClientSession s) {
        return s != null && s.isValid();
    }
}
