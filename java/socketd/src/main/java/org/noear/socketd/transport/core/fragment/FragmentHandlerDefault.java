package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.utils.IoUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 数据分片默认实现（可以重写，把大流先缓存到磁盘以节省内存）
 *
 * @author noear
 * @since 2.0
 */
public class FragmentHandlerDefault implements FragmentHandler {
    /**
     * 获取一个分片
     *
     * @param fragmentIndex 从1开始
     */
    @Override
    public Entity nextFragment(Channel channel, int fragmentIndex, MessageInternal message) throws IOException {

        ByteArrayOutputStream fragmentBuf = new ByteArrayOutputStream();
        IoUtils.transferTo(message.data(), fragmentBuf, 0, Constants.MAX_SIZE_FRAGMENT);
        byte[] fragmentBytes = fragmentBuf.toByteArray();
        if (fragmentBytes.length == 0) {
            return null;
        }
        EntityDefault fragmentEntity = new EntityDefault().data(fragmentBytes);
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
        FragmentAggregator aggregator = channel.getAttachment(message.sid());
        if (aggregator == null) {
            aggregator = new FragmentAggregator(message);
            channel.setAttachment(aggregator.getSid(), aggregator);
        }

        aggregator.add(index, message);

        if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
            //长度不够，等下一个分片包
            return null;
        } else {
            //重置为聚合帖
            return aggregator.get();
        }
    }
}