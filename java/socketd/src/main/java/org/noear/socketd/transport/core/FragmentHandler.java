package org.noear.socketd.transport.core;

import java.io.IOException;

/**
 * 数据分片处理（分片必须做，聚合可开关）
 *
 * @author noear
 * @since 2.0
 */
public interface FragmentHandler {
    /**
     * 获取下个分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（由导引安排，从1按序递进）
     * @param message       总包消息
     */
    Entity nextFragment(Channel channel, int fragmentIndex, MessageInternal message) throws IOException;

    /**
     * 聚合所有分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    Frame aggrFragment(Channel channel, int fragmentIndex, MessageInternal message) throws IOException;

    /**
     * 聚合启用
     */
    boolean aggrEnable();
}
