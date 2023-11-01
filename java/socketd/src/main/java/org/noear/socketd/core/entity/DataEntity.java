package org.noear.socketd.core.entity;

import org.noear.socketd.core.Constants;

/**
 * 数据实体（省去了元信息的输入）
 *
 * @author noear
 * @since 2.0
 */
public class DataEntity extends EntityDefault {
    public DataEntity(byte[] data) {
        super(Constants.DEF_META_STRING, data);
    }
}
