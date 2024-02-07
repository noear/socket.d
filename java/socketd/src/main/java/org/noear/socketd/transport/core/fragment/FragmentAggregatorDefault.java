package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.exception.SocketDCodecException;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.entity.MessageBuilder;
import org.noear.socketd.utils.StrUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 分片聚合器
 *
 * @author noear
 * @since 2.0
 */
public class FragmentAggregatorDefault implements FragmentAggregator {
    //主导消息
    private MessageInternal main;
    //分片列表
    private List<FragmentHolder> fragmentHolders = new ArrayList<>();
    //数据流大小
    private int dataStreamSize;
    //数据总长度
    private int dataLength;

    public FragmentAggregatorDefault(MessageInternal main) {
        this.main = main;
        String dataLengthStr = main.meta(EntityMetas.META_DATA_LENGTH);

        if (StrUtils.isEmpty(dataLengthStr)) {
            throw new SocketDCodecException("Missing '" + EntityMetas.META_DATA_LENGTH + "' meta, event=" + main.event());
        }

        this.dataLength = Integer.parseInt(dataLengthStr);
    }

    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    @Override
    public String getSid() {
        return main.sid();
    }

    /**
     * 数据流大小
     */
    @Override
    public int getDataStreamSize() {
        return dataStreamSize;
    }

    /**
     * 数据总长度
     */
    @Override
    public int getDataLength() {
        return dataLength;
    }

    /**
     * 添加帧
     */
    @Override
    public void add(int index, MessageInternal message) throws IOException {
        //添加分片
        fragmentHolders.add(new FragmentHolder(index, message));
        //添加计数
        dataStreamSize = dataStreamSize + message.dataSize();
    }

    /**
     * 获取聚合后的帧
     */
    @Override
    public Frame get() throws IOException {
        //排序
        fragmentHolders.sort(Comparator.comparing(fh -> fh.getIndex()));

        //创建聚合流
        ByteBuffer dataBuffer = ByteBuffer.allocate(dataLength);

        //添加分片数据
        for (FragmentHolder fh : fragmentHolders) {
            dataBuffer.put(fh.getMessage().data().array());
        }

        //索引番转
        dataBuffer.flip();

        EntityDefault entity = new EntityDefault().metaMapPut(main.metaMap()).dataSet(dataBuffer);
        entity.metaMap().remove(EntityMetas.META_DATA_FRAGMENT_IDX);
        //返回
        return new Frame(main.flag(), new MessageBuilder()
                .flag(main.flag())
                .sid(main.sid())
                .event(main.event())
                .entity(entity)
                .build());
    }
}