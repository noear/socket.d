package org.noear.socketd.core.entity;

import java.nio.charset.StandardCharsets;

/**
 * 字符串数据实体
 *
 * @author noear
 * @since 2.0
 */
public class StringEntity extends DataEntity {
    public StringEntity(String str) {
        super(str.getBytes(StandardCharsets.UTF_8));
    }
}
