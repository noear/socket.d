package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.Entity;

/**
 * @author noear
 * @since 2.0
 */
public class EntityBuilder extends Entity {
    public EntityBuilder() {
        super();
    }

    public EntityBuilder header(String header) {
        this.header = header;
        return this;
    }

    public EntityBuilder body(byte[] body) {
        this.body = body;
        return this;
    }
}
