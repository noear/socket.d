package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.EntityDefault;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 数据分片监时文件实现
 *
 * @author noear
 * @since 2.1
 */
public class FragmentHandlerTempfile implements FragmentHandler {
    /**
     * 获取一个分片
     *
     * @param fragmentIndex 从1开始
     */
    @Override
    public Entity nextFragment(Channel channel, int fragmentIndex, MessageInternal message) throws IOException {
        ByteBuffer dataBuffer = readFragmentData(message.data(), channel.getConfig().getFragmentSize());
        if (dataBuffer.limit() == 0) {
            return null;
        }

        EntityDefault fragmentEntity = new EntityDefault().data(dataBuffer);
        if (fragmentIndex == 1) {
            fragmentEntity.metaMap(message.metaMap());
        }
        fragmentEntity.meta(EntityMetas.META_DATA_FRAGMENT_IDX, String.valueOf(fragmentIndex));

        return fragmentEntity;
    }

    /**
     * 聚合分片（可以重写，把大流先缓存到磁盘以节省内存）
     */
    @Override
    public Frame aggrFragment(Channel channel, int index, MessageInternal message) throws IOException {
        FragmentAggregatorTempfile aggregator = channel.getAttachment(message.sid());
        if (aggregator == null) {
            aggregator = new FragmentAggregatorTempfile(message);
            channel.setAttachment(aggregator.getSid(), aggregator);
        }

        aggregator.add(index, message);

        if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
            //长度不够，等下一个分片包
            return null;
        } else {
            //重置为聚合帖
            channel.setAttachment(message.sid(), null);
            return aggregator.get();
        }
    }

    @Override
    public boolean aggrEnable() {
        return true;
    }

    private ByteBuffer readFragmentData(ByteBuffer ins, int maxSize) {
        int size = 0;
        if (ins.remaining() > maxSize) {
            size = maxSize;
        } else {
            size = ins.remaining();
        }

        byte[] bytes = new byte[size];
        ins.get(bytes);

        return ByteBuffer.wrap(bytes);
    }
}