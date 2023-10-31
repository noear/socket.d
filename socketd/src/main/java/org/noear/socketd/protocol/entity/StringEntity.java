package org.noear.socketd.protocol.entity;

import org.noear.socketd.protocol.Constants;

import java.nio.charset.StandardCharsets;

/**
 * 字符串实体
 *
 * @author noear
 * @since 2.0
 */
public class StringEntity extends EntityDefault {
    public StringEntity(String str) {
        super(Constants.DEF_META_STRING, str.getBytes(StandardCharsets.UTF_8));
    }
}
