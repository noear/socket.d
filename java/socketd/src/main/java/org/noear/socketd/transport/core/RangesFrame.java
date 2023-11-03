package org.noear.socketd.transport.core;

import java.io.IOException;

/**
 * 分片聚合帧
 *
 * @author noear
 * @since 2.0
 */
public interface RangesFrame {
    /**
     * 获取聚合后的帧
     */
    Frame getFrame() throws IOException;
}
