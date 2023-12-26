package org.noear.socketd.transport.core;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * 消息实体（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public interface Entity {
    /**
     * at
     *
     * @since 2.1
     */
    default String at() {
        return meta("@");
    }

    /**
     * 获取元信息字符串（queryString style）
     */
    String metaString();

    /**
     * 获取元信息字典
     */
    Map<String, String> metaMap();

    /**
     * 获取元信息
     */
    String meta(String name);

    /**
     * 获取元信息或默认
     */
    String metaOrDefault(String name, String def);

    /**
     * 添加元信息
     * */
    void putMeta(String name, String val);

    /**
     * 获取数据
     */
    ByteBuffer data();

    /**
     * 获取数据并转为字符串
     */
    String dataAsString();

    /**
     * 获取数据并转为字节数组
     */
    byte[] dataAsBytes();

    /**
     * 获取数据长度
     */
    int dataSize();

    /**
     * 释放资源
     */
    void release();
}
