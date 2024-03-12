package org.noear.socketd.transport.core;

import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.socketd.transport.core.entity.StringEntity;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * 消息实体（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
public interface Entity {
    // >>> 不需要迁移
    static StringEntity of(String data){
        return new StringEntity(data);
    }

    static FileEntity of(File data) throws IOException {
        return new FileEntity(data);
    }

    static EntityDefault of(byte[] data){
        return new EntityDefault().dataSet(data);
    }

    static EntityDefault of(ByteBuffer data){
        return new EntityDefault().dataSet(data);
    }

    static EntityDefault of(){
        return new EntityDefault();
    }
    // <<< 不需要迁移

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
     * 获取元信息并转为 int
     */
    default int metaAsInt(String name) {
        return Integer.parseInt(metaOrDefault(name, "0"));
    }

    /**
     * 获取元信息并转为 long
     */
    default long metaAsLong(String name) {
        return Long.parseLong(metaOrDefault(name, "0"));
    }

    /**
     * 获取元信息并转为 float
     */
    default float metaAsFloat(String name) {
        return Float.parseFloat(metaOrDefault(name, "0"));
    }

    /**
     * 获取元信息并转为 double
     */
    default double metaAsDouble(String name) {
        return Double.parseDouble(metaOrDefault(name, "0"));
    }

    /**
     * 放置元信息
     * */
    void putMeta(String name, String val);

    /**
     * 删除元信息
     * */
    void delMeta(String name);

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
    void release() throws IOException;
}
