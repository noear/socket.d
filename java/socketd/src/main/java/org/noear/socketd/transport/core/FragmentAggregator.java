package org.noear.socketd.transport.core;

import java.io.IOException;

/**
 * 分片聚合器
 *
 * @author noear
 * @since 2.1
 */
public interface FragmentAggregator {
    /**
     * 获取流Id
     */
    String getSid();

    /**
     * 数据流大小
     */
    int getDataStreamSize();

    /**
     * 数据总长度
     */
    int getDataLength();

    /**
     * 添加分片
     */
    void add(int index, MessageInternal message) throws IOException;

    /**
     * 获取聚合帧
     */
    Frame get() throws IOException;
}
