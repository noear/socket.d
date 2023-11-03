package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.exception.SocketdCodecException;
import org.noear.socketd.utils.IoUtils;
import org.noear.socketd.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 实体默认实现
 *
 * @author noear
 * @since 2.0
 */
public abstract class BaseEntity implements Entity {
    private boolean metaChanged = false;

    protected String metaString;
    protected Map<String, String> metaMap;
    protected InputStream data;
    protected int dataSize;

    /**
     * Header
     */
    @Override
    public String getMetaString() {
        if (metaChanged) {
            StringBuilder buf = new StringBuilder();

            getMetaMap().forEach((key, val) -> {
                buf.append(key).append("=").append(val).append("&");
            });

            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }

            metaString = buf.toString();
            metaChanged = false;
        }

        return metaString;
    }

    public void setMetaMap(Map<String, String> metaMap) {
        this.metaMap = metaMap;
        metaString = null;
        metaChanged = true;
    }

    /**
     * Header as map
     */
    @Override
    public Map<String, String> getMetaMap() {
        if (metaMap == null) {
            metaMap = new LinkedHashMap<>();
            metaChanged = false;

            //此处要优化
            if (Utils.isNotEmpty(metaString)) {
                for (String kvStr : metaString.split("&")) {
                    String[] kv = kvStr.split("=");
                    if (kv.length > 1) {
                        metaMap.put(kv[0], kv[1]);
                    } else {
                        metaMap.put(kv[0], "");
                    }
                }
            }
        }

        return metaMap;
    }

    /**
     * 设置元信息
     */
    public void putMeta(String name, String val) {
        getMetaMap().put(name, val);
        metaChanged = true;
    }

    /**
     * 获取元信息
     */
    @Override
    public String getMeta(String name) {
        return getMetaMap().get(name);
    }

    /**
     * 获取数据
     */
    @Override
    public InputStream getData() {
        return data;
    }

    @Override
    public String getDataAsString() {
        try {
            return IoUtils.transferToString(getData());
        } catch (IOException e) {
            throw new SocketdCodecException(e);
        }
    }

    /**
     * 获取数据长度
     */
    @Override
    public int getDataSize() {
        return dataSize;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "meta='" + getMetaString() + '\'' +
                ", data=byte[" + dataSize + ']' + //避免内容太大，影响打印
                '}';
    }
}
