package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Entity;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author noear
 * @since 2.0
 */
public class EndEntity implements Entity {
    private Entity entity;

    public EndEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public String metaString() {
        return entity.metaString();
    }

    @Override
    public Map<String, String> metaMap() {
        return entity.metaMap();
    }

    @Override
    public String meta(String name) {
        return entity.meta(name);
    }

    @Override
    public String metaOrDefault(String name, String def) {
        return entity.metaOrDefault(name, def);
    }

    @Override
    public ByteBuffer data() {
        return entity.data();
    }

    @Override
    public String dataAsString() {
        return entity.dataAsString();
    }

    @Override
    public byte[] dataAsBytes() {
        return entity.dataAsBytes();
    }

    @Override
    public int dataSize() {
        return entity.dataSize();
    }

    @Override
    public void release() {
        entity.release();
    }
}
