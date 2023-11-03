package org.noear.socketd.transport.core.entity;

import java.io.ByteArrayInputStream;

/**
 * 实体默认实现
 *
 * @author noear
 * @since 2.0
 */
public class EntityDefault extends BaseEntity {
    public EntityDefault() {
    }

    public EntityDefault(String metaString, byte[] data) {
        this.metaString = metaString;
        this.data = new ByteArrayInputStream(data);
        this.dataSize = data.length;
    }


    public void setData(byte[] data) {
        this.data = new ByteArrayInputStream(data);
        this.dataSize = data.length;
    }
}
