package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.exception.SocketdCodecException;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.utils.IoUtils;
import org.noear.socketd.utils.Utils;

import java.io.ByteArrayInputStream;
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
    //主帧
    private Frame main;
    private List<FragmentHolder> fragmentHolders = new ArrayList<>();
    //数据流大小
    private int dataStreamSize;
    //数据总长度
    private int dataLength;

    public FragmentAggregator(Frame main) {
        this.main = main;
        String dataLengthStr = main.getMessage().getEntity().getMeta(EntityMetas.META_DATA_LENGTH);

        if (Utils.isEmpty(dataLengthStr)) {
            throw new SocketdCodecException("Missing '" + EntityMetas.META_DATA_LENGTH + "' meta, topic=" + main.getMessage().getTopic());
        }

        this.dataLength = Integer.parseInt(dataLengthStr);
    }

    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    public String getSid() {
        return main.getMessage().getSid();
    }

    /**
     * 数据流大小
     */
    public int getDataStreamSize() {
        return dataStreamSize;
    }

    /**
     * 数据长度
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
            IoUtils.transferTo(fh.getFrame().getMessage().getEntity().getData(), dataStream);
        }

        //转流并输出
        ByteArrayInputStream inputStream = new ByteArrayInputStream(dataStream.toByteArray());

        return new Frame(main.getFlag(), new MessageDefault()
                .flag(main.getFlag())
                .sid(main.getMessage().getSid())
                .topic(main.getMessage().getTopic())
                .entity(new EntityDefault().metaMap(main.getMessage().getEntity().getMetaMap()).data(inputStream)));
    }

    /**
     * 添加帧
     */
    public void add(int index, Frame frame) throws IOException {
        //添加分片
        fragmentHolders.add(new FragmentHolder(index, frame));
        //添加计数
        dataStreamSize = dataStreamSize + frame.getMessage().getEntity().getDataSize();
    }
}
