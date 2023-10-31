package org.noear.socketd.protocol.entity;

import org.noear.socketd.protocol.Constants;

/**
 * 头实体
 *
 * @author noear
 * @since 2.0
 */
public class MetaEntity extends EntityDefault {
    public MetaEntity(String metaString) {
        super(metaString, Constants.DEF_DATA);
    }
}
