package org.noear.socketd.transport.core;

import java.io.InputStream;
import java.util.Map;

/**
 * 消息实体（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public interface Entity {
    /**
     * 获取元信息字符串（queryString style）
     */
    String getMetaString();

    /**
     * 获取元信息字典
     */
    Map<String, String> getMetaMap();

    /**
     * 获取元信息
     */
    String getMeta(String name);

    /**
     * 获取元信息或默认
     */
    String getMetaOrDefault(String name, String def);

    /**
     * 获取数据
     */
    InputStream getData();

    /**
     * 获取数据并转为字符串
     */
    String getDataAsString();

    /**
     * 获取数据并转为字节数组
     */
    byte[] getDataAsBytes();

    /**
     * 获取数据长度
     */
    int getDataSize();
}
