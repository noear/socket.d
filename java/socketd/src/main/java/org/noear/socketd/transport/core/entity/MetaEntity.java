package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;

/**
 * 元信息实体（省用了数据的录入）
 *
 * @author noear
 * @since 2.0
 */
public class MetaEntity extends EntityDefault {
    public MetaEntity(String metaString) {
        super(metaString, Constants.DEF_DATA);
    }
}
