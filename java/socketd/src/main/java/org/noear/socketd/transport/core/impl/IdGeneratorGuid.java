package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.IdGenerator;

import java.util.UUID;

/**
 * @author noear
 * @since 2.0
 */
public class IdGeneratorGuid implements IdGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
