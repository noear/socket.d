package org.noear.socketd.transport.core;

/**
 * 流管理器
 *
 * @author noear
 * @since 2.0
 */
public interface StreamManger {
    /**
     * 添加流接收器
     *
     * @param sid      流Id
     * @param acceptor 流接收器
     */
    void addAcceptor(String sid, StreamAcceptorBase acceptor);

    /**
     * 获取流接收器
     *
     * @param sid 流Id
     */
    StreamAcceptor getAcceptor(String sid);

    /**
     * 移除流接收器
     *
     * @param sid 流Id
     */
    void removeAcceptor(String sid);
}
