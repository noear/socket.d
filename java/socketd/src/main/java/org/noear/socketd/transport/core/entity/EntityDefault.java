package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体默认实现
 *
 * @author noear
 * @since 2.0
 */
public class EntityDefault implements Entity {

    private Map<String, String> metaMap;
    private String metaString = Constants.DEF_META_STRING;
    private boolean metaStringChanged = false;
    private ByteBuffer data = Constants.DEF_DATA;
    private int dataSize = 0;

    public EntityDefault at(String name) {
        meta("@", name);
        return this;
    }

    public EntityDefault metaString(String metaString) {
        this.metaMap = null;
        this.metaString = metaString;
        this.metaStringChanged = false;
        return this;
    }

    /**
     * 获取元信息字符串（queryString style）
     */
    @Override
    public String metaString() {
        if (metaStringChanged) {
            StringBuilder buf = new StringBuilder();

            List<String> metaKeys = new ArrayList<>(metaMap().keySet());

            for (String name : metaKeys) {
                String val = metaMap().get(name);
                buf.append(name).append("=").append(val).append("&");
            }

            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }

            metaString = buf.toString();
            metaStringChanged = false;
        }

        return metaString;
    }

    /**
     * 设置元信息字典
     *
     * @param metaMap 元信息字典
     */
    public EntityDefault metaMap(Map<String, String> metaMap) {
        this.metaMap().putAll(metaMap);
        this.metaString = null;
        this.metaStringChanged = true;
        return this;
    }

    /**
     * 获取元信息字典
     */
    @Override
    public Map<String, String> metaMap() {
        if (metaMap == null) {
            metaMap = new ConcurrentHashMap<>();
            metaStringChanged = false;

            //此处要优化
            if (Utils.isNotEmpty(metaString)) {
                for (String kvStr : metaString.split("&")) {
                    int idx = kvStr.indexOf('=');
                    if (idx > 0) {
                        metaMap.put(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                    }
                }
            }
        }

        return metaMap;
    }

    /**
     * 设置元信息
     *
     * @param name 名字
     * @param val  值
     */
    public EntityDefault meta(String name, String val) {
        metaMap().put(name, val);
        metaStringChanged = true;
        return this;
    }

    /**
     * 获取元信息
     *
     * @param name 名字
     */
    @Override
    public String meta(String name) {
        return metaMap().get(name);
    }

    /**
     * 获取元信息或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    @Override
    public String metaOrDefault(String name, String def) {
        return metaMap().getOrDefault(name, def);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public EntityDefault data(byte[] data) {
        this.data = ByteBuffer.wrap(data);
        this.dataSize = data.length;
        if (dataSize > Constants.MAX_SIZE_DATA) {
            meta(EntityMetas.META_DATA_LENGTH, String.valueOf(dataSize));
        }
        return this;
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public EntityDefault data(ByteBuffer data) {
        this.data = data;
        this.dataSize = data.limit();
        if (dataSize > Constants.MAX_SIZE_DATA) {
            meta(EntityMetas.META_DATA_LENGTH, String.valueOf(dataSize));
        }
        return this;
    }

    /**
     * 获取数据（若多次复用，需要reset）
     */
    @Override
    public ByteBuffer data() {
        return data;
    }

    /**
     * 获取数据并转成字符串
     */
    @Override
    public String dataAsString() {
        if (dataAsString == null) {
            dataAsString = new String(data.array(), StandardCharsets.UTF_8);
        }

        return dataAsString;
    }

    private String dataAsString;

    @Override
    public byte[] dataAsBytes() {
        return data.array();
    }

    /**
     * 获取数据长度
     */
    @Override
    public int dataSize() {
        return dataSize;
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {

    }

    @Override
    public String toString() {
        return "Entity{" +
                "meta='" + metaString() + '\'' +
                ", data=byte[" + dataSize + ']' + //避免内容太大，影响打印
                '}';
    }
}