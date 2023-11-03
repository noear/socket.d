package org.noear.socketd.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据分片处理
 *
 * @author noear
 * @since 2.0
 */
public interface RangesHandler {
    Entity nextRange(Config config, AtomicReference<Integer> rangeIndex, Entity entity) throws IOException;
}
