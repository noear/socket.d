package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.KeyGenerator;

import java.util.UUID;

/**
 * @author noear
 * @since 2.0
 */
public class KeyGeneratorGuid implements KeyGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
