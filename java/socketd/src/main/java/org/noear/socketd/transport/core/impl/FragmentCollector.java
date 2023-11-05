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

/**
 * 分片收集器
 *
 * @author noear
 * @since 2.0
 */
public class FragmentCollector {
    //主帧
    private Frame main;
    //数据聚合流
    private ByteArrayOutputStream dataStream;
    //数据流大小
    private int dataStreamSize;
    //数据总长度
    private int dataLength = 0;

    public FragmentCollector(Frame main) {
        this.main = main;
        this.dataStream = new ByteArrayOutputStream();
        String dataLengthStr = main.getMessage().getEntity().getMeta(EntityMetas.META_DATA_LENGTH);

        if (Utils.isEmpty(dataLengthStr)) {
            throw new SocketdCodecException("Missing '" + EntityMetas.META_DATA_LENGTH + "' meta");
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
     * */
    public Frame get() throws IOException {
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
    public void add(Frame frame) throws IOException {
        //添加计数
        dataStreamSize = dataStreamSize + frame.getMessage().getEntity().getDataSize();
        //添加数据
        IoUtils.transferTo(frame.getMessage().getEntity().getData(), dataStream);
    }
}
