package org.noear.socketd.transport.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据分片处理
 *
 * @author noear
 * @since 2.0
 */
public interface FragmentHandler {
    /**
     * 获取下个分片
     * */
    Entity nextFragment(Config config, AtomicReference<Integer> fragmentIndex, Entity entity) throws IOException;

    /**
     * 聚合所有分片
     * */
    Frame aggrFragments(Channel channel, Frame frame) throws IOException;
}
