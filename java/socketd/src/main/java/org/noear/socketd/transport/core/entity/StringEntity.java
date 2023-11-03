package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;

import java.nio.charset.StandardCharsets;

/**
 * 字符串数据实体
 *
 * @author noear
 * @since 2.0
 */
public class StringEntity extends EntityDefault {
    public StringEntity(String str) {
        super(Constants.DEF_META_STRING, str.getBytes(StandardCharsets.UTF_8));
    }
}
