package org.noear.socketd;

/**
 * 会话工厂
 *
 * @author noear
 * @since 2.0
 */
public interface SessionFactory {
    Session create(String url);
    Session create(Session session);
}
