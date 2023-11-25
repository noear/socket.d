package org.noear.socketd.transport.core.fragment;

import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.exception.SocketdCodecException;
import org.noear.socketd.transport.core.MessageInternal;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.internal.MessageDefault;
import org.noear.socketd.utils.IoUtils;
import org.noear.socketd.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 分片聚合器
 *
 * @author noear
 * @since 2.0
 */
public class FragmentAggregator {
    //主导消息
    private MessageInternal main;
    //分片列表
    private List<FragmentHolder> fragmentHolders = new ArrayList<>();
    //数据流大小
    private int dataStreamSize;
    //数据总长度
    private int dataLength;

    public FragmentAggregator(MessageInternal main) {
        this.main = main;
        String dataLengthStr = main.meta(EntityMetas.META_DATA_LENGTH);

        if (Utils.isEmpty(dataLengthStr)) {
            throw new SocketdCodecException("Missing '" + EntityMetas.META_DATA_LENGTH + "' meta, event=" + main.event());
        }

        this.dataLength = Integer.parseInt(dataLengthStr);
    }

    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    public String getSid() {
        return main.sid();
    }

    /**
     * 数据流大小
     */
    public int getDataStreamSize() {
        return dataStreamSize;
    }

    /**
     * 数据总长度
     */
    public int getDataLength() {
        return dataLength;
    }

    /**
     * 获取聚合后的帧
     */
    public Frame get() throws IOException {
        //排序
        fragmentHolders.sort(Comparator.comparing(fh -> fh.getIndex()));

        //创建聚合流
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream(dataLength);

        //添加分片数据
        for (FragmentHolder fh : fragmentHolders) {
            IoUtils.transferTo(fh.getMessage().data(), dataStream);
        }

        //返回
        return new Frame(main.flag(), new MessageDefault()
                .flag(main.flag())
                .sid(main.sid())
                .event(main.event())
                .entity(new EntityDefault().metaMap(main.metaMap()).data(dataStream.toByteArray())));
    }

    /**
     * 添加帧
     */
    public void add(int index, MessageInternal message) throws IOException {
        //添加分片
        fragmentHolders.add(new FragmentHolder(index, message));
        //添加计数
        dataStreamSize = dataStreamSize + message.dataSize();
    }
}
