package org.noear.socketd.transport.core;


/**
 * 消息
 *
 * @author noear
 * @since 2.0
 */
public interface Message extends Entity{

    /**
     * 是否为请求
     */
    boolean isRequest();

    /**
     * 是否为订阅
     */
    boolean isSubscribe();

    /**
     * 范围开始
     * */
    default int rangeStart(){
        return metaAsInt(EntityMetas.META_RANGE_START);
    }

    /**
     * 范围大小
     * */
    default int rangeSize(){
        return metaAsInt(EntityMetas.META_RANGE_SIZE);
    }

    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    String sid();

    /**
     * 获取消息事件
     */
    String event();

    /**
     * 获取消息实体（有时需要获取实体）
     */
    Entity entity();
}
