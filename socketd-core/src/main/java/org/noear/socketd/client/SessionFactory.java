package org.noear.socketd.client;

import org.noear.socketd.Session;

/**
 * 会话工厂
 *
 * @author noear
 * @since 2.0
 */
public interface SessionFactory {
    /**
     * 支持的地址架构
     */
    String[] schemes();

    /**
     * 驱动类型
     */
    Class<?> driveType();

    /**
     * 创建会话
     */
    Session createSession(String url);
}
