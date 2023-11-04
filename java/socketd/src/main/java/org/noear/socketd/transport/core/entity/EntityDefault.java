package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.exception.SocketdCodecException;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.utils.IoUtils;
import org.noear.socketd.utils.Utils;

import java.io.ByteArrayInputStream;
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
public class EntityDefault implements Entity {

    private Map<String, String> metaMap;
    private String metaString = Constants.DEF_META_STRING;
    private boolean metaStringChanged = false;
    private InputStream data = Constants.DEF_DATA;
    private int dataSize = 0;

    public EntityDefault metaString(String metaString) {
        this.metaMap = null;
        this.metaString = metaString;
        this.metaStringChanged = false;
        return this;
    }

    /**
     * Header
     */
    @Override
    public String getMetaString() {
        if (metaStringChanged) {
            StringBuilder buf = new StringBuilder();

            getMetaMap().forEach((name, val) -> {
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

    public EntityDefault metaMap(Map<String, String> metaMap) {
        this.metaMap = metaMap;
        this.metaString = null;
        this.metaStringChanged = true;
        return this;
    }

    /**
     * Header as map
     */
    @Override
    public Map<String, String> getMetaMap() {
        if (metaMap == null) {
            metaMap = new LinkedHashMap<>();
            metaStringChanged = false;

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
        metaStringChanged = true;
    }

    /**
     * 获取元信息
     */
    @Override
    public String getMeta(String name) {
        return getMetaMap().get(name);
    }

    public EntityDefault data(byte[] data) {
        this.data = new ByteArrayInputStream(data);
        this.dataSize = data.length;
        return this;
    }

    public EntityDefault data(InputStream data) throws IOException{
        this.data = data;
        this.dataSize = data.available();
        putMeta(EntityMetas.META_DATA_LENGTH, String.valueOf(dataSize));
        return this;
    }

    /**
     * 获取数据（如果要多次复用，重用之前需要reset）
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
