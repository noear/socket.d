package org.noear.socketd.transport.core;

/**
 * 流管理器
 *
 * @author noear
 * @since 2.0
 */
public interface StreamManger {
    /**
     * 添加流
     *
     * @param sid    流Id
     * @param stream 流
     */
    void addStream(String sid, StreamInternal stream);

    /**
     * 获取流
     *
     * @param sid 流Id
     */
    StreamInternal getStream(String sid);

    /**
     * 移除流
     *
     * @param sid 流Id
     */
    void removeStream(String sid);
}
