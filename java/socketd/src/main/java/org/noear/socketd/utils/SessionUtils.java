package org.noear.socketd.utils;

import org.noear.socketd.transport.client.ClientSession;

/**
 * @author noear
 * @since 2.4
 */
public class SessionUtils {
    /**
     * 是否活动的
     */
    public static boolean isActive(ClientSession s) {
        return s != null && s.isValid() && !s.isClosing();
    }

    /**
     * 是否有效的
     */
    public static boolean isValid(ClientSession s) {
        return s != null && s.isValid();
    }
}
