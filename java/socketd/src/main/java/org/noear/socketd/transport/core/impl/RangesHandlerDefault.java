package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.utils.IoUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据分片默认实现
 *
 * @author noear
 * @since 2.0
 */
public class RangesHandlerDefault implements RangesHandler {
    @Override
    public Entity nextRange(Config config, AtomicReference<Integer> rangeIndex, Entity entity) throws IOException {
        rangeIndex.set(rangeIndex.get() + 1);

        ByteArrayOutputStream rangeBuf = new ByteArrayOutputStream();
        IoUtils.transferTo(entity.getData(), rangeBuf, 0, config.getRangeSize());
        byte[] rangeBytes = rangeBuf.toByteArray();
        if (rangeBytes.length == 0) {
            return null;
        }
        EntityDefault rangeEntity = new EntityDefault().data(rangeBytes);
        if (rangeIndex.get() == 1) {
            rangeEntity.metaMap(entity.getMetaMap());
        }
        rangeEntity.putMeta(EntityMetas.META_DATA_RANGE_IDX, String.valueOf(rangeIndex));
        return rangeEntity;
    }


    @Override
    public RangesFrame aggrRanges(Channel channel, Frame frame) throws IOException {
        RangesFrameDefault aggregator = channel.getAttachment(frame.getMessage().getSid());
        if (aggregator == null) {
            aggregator = new RangesFrameDefault(frame);
            channel.setAttachment(aggregator.getKey(), aggregator);
        }

        aggregator.add(frame);

        if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
            //长度不够，等下一个分片包
            return null;
        } else {
            //重置为聚合帖
            return aggregator;
        }
    }
}
