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
     * 获取元信息
     */
    Map<String, String> getMetaMap();

    /**
     * 获取元信息
     */
    String getMeta(String name);

    /**
     * 获取数据
     */
    InputStream getData();

    /**
     * 获取数据并转为这符串
     */
    String getDataAsString();

    /**
     * 获取数据长度
     */
    int getDataSize();
}
