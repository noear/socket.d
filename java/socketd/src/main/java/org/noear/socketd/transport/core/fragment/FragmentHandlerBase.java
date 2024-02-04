package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.stream.StreamInternal;
import org.noear.socketd.utils.IoConsumer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

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
     * @param channel 通道
     * @param message 总包消息
     */
    @Override
    public void spliFragment(Channel channel, StreamInternal stream, MessageInternal message, IoConsumer<Entity> consumer) throws IOException {
        if (message.dataSize() > channel.getConfig().getFragmentSize()
                || message.data() instanceof MappedByteBuffer) {
            // MappedByteBuffer 的数据，也需要提前转出来
            int fragmentTotal = message.dataSize() / channel.getConfig().getFragmentSize();
            if (message.dataSize() % channel.getConfig().getFragmentSize() > 0) {
                fragmentTotal++;
            }

            int fragmentIndex = 0;
            while (true) {
                //生产分片
                fragmentIndex++;
                ByteBuffer dataBuffer = readFragmentData(message.data(), channel.getConfig().getFragmentSize());
                if (dataBuffer == null || dataBuffer.limit() == 0) {
                    return;
                }

                EntityDefault fragmentEntity = new EntityDefault().dataSet(dataBuffer);
                if (fragmentIndex == 1) {
                    fragmentEntity.metaMapPut(message.metaMap());
                }

                if (fragmentTotal > 1) {
                    //数量大于1才算真正的分版
                    fragmentEntity.metaPut(EntityMetas.META_DATA_FRAGMENT_IDX, String.valueOf(fragmentIndex));
                    fragmentEntity.metaPut(EntityMetas.META_DATA_FRAGMENT_TOTAL, String.valueOf(fragmentTotal));
                }

                consumer.accept(fragmentEntity);
                if (stream != null) {
                    stream.onProgress(true, fragmentIndex, fragmentTotal);
                }
            }
        } else {
            consumer.accept(message);
            if (stream != null) {
                stream.onProgress(true, 1, 1);
            }
        }
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
            aggregator = createFragmentAggregator(message);
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
