package org.noear.socketd.transport.core;

import java.util.Map;

/**
 * 握手信息内部接口
 *
 * @author noear
 * @since 2.0
 */
public interface HandshakeInternal extends Handshake {
    /**
     * 获取消息源
     */
    MessageInternal getSource();

    /**
     * 获取输出元信息
     */
    Map<String, String> getOutMetaMap();
}
