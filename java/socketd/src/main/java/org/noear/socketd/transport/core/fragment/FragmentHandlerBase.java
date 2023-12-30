package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.EntityDefault;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 数据分片处理基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class FragmentHandlerBase implements FragmentHandler {
    /**
     * 获取下个分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（由导引安排，从1按序递进）
     * @param message       总包消息
     */
    @Override
    public Entity nextFragment(Channel channel, int fragmentIndex, MessageInternal message) throws IOException {
        ByteBuffer dataBuffer = readFragmentData(message.data(), channel.getConfig().getFragmentSize());
        if (dataBuffer == null || dataBuffer.limit() == 0) {
            return null;
        }

        EntityDefault fragmentEntity = new EntityDefault().dataSet(dataBuffer);
        if (fragmentIndex == 1) {
            fragmentEntity.metaMapPut(message.metaMap());
        }
        fragmentEntity.metaPut(EntityMetas.META_DATA_FRAGMENT_IDX, String.valueOf(fragmentIndex));

        return fragmentEntity;
    }

    /**
     * 聚合所有分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    @Override
    public Frame aggrFragment(Channel channel, int fragmentIndex, MessageInternal message) throws IOException {
        FragmentAggregator aggregator = channel.getAttachment(message.sid());
        if (aggregator == null) {
            aggregator = new FragmentAggregatorDefault(message);
            channel.putAttachment(aggregator.getSid(), aggregator);
        }

        aggregator.add(fragmentIndex, message);

        if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
            //长度不够，等下一个分片包
            return null;
        } else {
            //重置为聚合帖
            channel.putAttachment(message.sid(), null);
            return aggregator.get();
        }
    }

    /**
     * 创建分片聚合器
     */
    protected abstract FragmentAggregator createFragmentAggregator(MessageInternal message) throws IOException;

    /**
     * 读取分版数据
     */
    protected ByteBuffer readFragmentData(ByteBuffer ins, int maxSize) {
        int size = 0;
        if (ins.remaining() > maxSize) {
            size = maxSize;
        } else {
            size = ins.remaining();
        }

        if (size == 0) {
            return null;
        }

        byte[] bytes = new byte[size];
        ins.get(bytes);

        return ByteBuffer.wrap(bytes);
    }
}
