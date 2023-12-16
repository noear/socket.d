package org.noear.socketd.transport.core;

/**
 * 答复实体
 *
 * @author noear
 * @since 2.1
 */
public interface Reply extends Entity {
    /**
     * 是否答复结束
     */
    boolean isEnd();
}
