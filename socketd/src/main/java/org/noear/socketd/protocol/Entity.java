package org.noear.socketd.protocol;

/**
 * 消息实体
 *
 * @author noear
 * @since 2.0
 */
public interface Entity {
    /**
     * 获取元信息字符串
     */
    String getMetaString();

    /**
     * 获取元信息
     */
    String getMeta(String name);

    /**
     * 获取数据
     */
    byte[] getData();
}
