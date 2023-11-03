package org.noear.socketd.transport.core.entity;

import org.noear.socketd.transport.core.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 流实体
 *
 * @author noear
 * @since 2.0
 */
public class StreamEntity extends BaseEntity {

    public StreamEntity(InputStream stream) throws IOException {
        this.data = stream;
        this.dataSize = stream.available();
        putMeta(Constants.META_DATA_LENGTH, String.valueOf(dataSize));
    }

    public StreamEntity(Map<String, String> metaMap, InputStream stream) throws IOException {
        this.data = stream;
        this.dataSize = stream.available();
        this.setMetaMap(metaMap);
    }
}
