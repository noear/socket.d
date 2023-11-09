package org.noear.socketd.transport.core.identifier;

import org.noear.socketd.transport.core.IdGenerator;

import java.util.UUID;

/**
 * Id 生成顺 guid 适配
 *
 * @author noear
 * @since 2.0
 */
public class GuidGenerator implements IdGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
