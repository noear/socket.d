package org.noear.socketd.core.entity;

import org.noear.socketd.core.Constants;

import java.io.IOException;
import java.io.InputStream;

/**
 * 流实体
 *
 * @author noear
 * @since 2.0
 */
public class StreamEntity extends BaseEntity {

    private int rangeIndex;

    public StreamEntity(InputStream stream) throws IOException {
        this.data = stream;
        this.dataSize = stream.available();
        putMeta(Constants.META_DATA_LENGTH, String.valueOf(dataSize));
    }
}
