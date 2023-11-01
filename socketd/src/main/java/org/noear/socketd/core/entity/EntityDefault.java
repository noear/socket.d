package org.noear.socketd.core.entity;

import org.noear.socketd.core.Entity;
import org.noear.socketd.utils.Utils;

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

    private boolean metaChanged = false;
    private String metaString;
    private byte[] data;

    public EntityDefault(String metaString, byte[] data) {
        this.metaString = metaString;
        this.data = data;
    }

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

    private Map<String, String> metaMap;

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
    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "meta='" + getMetaString() + '\'' +
                ", data=" + new String(getData(), StandardCharsets.UTF_8) +
                '}';
    }
}
