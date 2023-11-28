package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.exception.SocketdCodecException;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.buffer.BytesInputStream;
import org.noear.socketd.utils.IoUtils;
import org.noear.socketd.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private InputStream data = Constants.DEF_DATA;
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

            metaMap().forEach((name, val) -> {
                buf.append(name).append("=").append(val).append("&");
            });

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
            metaMap = new LinkedHashMap<>();
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
        this.data = new BytesInputStream(data);
        this.dataSize = data.length;
        if (dataSize > Constants.MAX_SIZE_FRAGMENT) {
            meta(EntityMetas.META_DATA_LENGTH, String.valueOf(dataSize));
        }
        return this;
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public EntityDefault data(InputStream data) throws IOException {
        this.data = data;
        this.dataSize = data.available();
        meta(EntityMetas.META_DATA_LENGTH, String.valueOf(dataSize));
        return this;
    }

    /**
     * 获取数据（若多次复用，需要reset）
     */
    @Override
    public InputStream data() {
        return data;
    }

    /**
     * 获取数据并转成字符串
     */
    @Override
    public String dataAsString() {
        try {
            if (dataAsString == null) {
                if (data() instanceof BytesInputStream) {
                    dataAsString = new String(((BytesInputStream) data()).bytes(), StandardCharsets.UTF_8);
                } else {
                    dataAsString = IoUtils.transferToString(data());
                }
            }

            return dataAsString;
        } catch (IOException e) {
            throw new SocketdCodecException(e);
        }
    }

    private String dataAsString;

    @Override
    public byte[] dataAsBytes() {
        try {
            if (data() instanceof BytesInputStream) {
                return ((BytesInputStream) data()).bytes();
            } else {
                return IoUtils.transferToBytes(data());
            }
        } catch (IOException e) {
            throw new SocketdCodecException(e);
        }
    }

    /**
     * 获取数据长度
     */
    @Override
    public int dataSize() {
        return dataSize;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "meta='" + metaString() + '\'' +
                ", data=byte[" + dataSize + ']' + //避免内容太大，影响打印
                '}';
    }
}