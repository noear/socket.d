package org.noear.socketd.transport.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据分片处理
 *
 * @author noear
 * @since 2.0
 */
public interface RangesHandler {
    /**
     * 获取下个分片
     * */
    Entity nextRange(Config config, AtomicReference<Integer> rangeIndex, Entity entity) throws IOException;

    /**
     * 聚合所有分片
     * */
    RangesFrame aggrRanges(Channel channel, Frame frame) throws IOException;
}
