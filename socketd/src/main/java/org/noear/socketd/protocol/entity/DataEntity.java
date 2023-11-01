package org.noear.socketd.protocol.entity;

import org.noear.socketd.protocol.Constants;

/**
 * 头实体
 *
 * @author noear
 * @since 2.0
 */
public class DataEntity extends EntityDefault {
    public DataEntity(byte[] data) {
        super(Constants.DEF_META_STRING, data);
    }
}
