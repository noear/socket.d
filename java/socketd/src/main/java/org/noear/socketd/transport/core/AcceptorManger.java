package org.noear.socketd.transport.core;

/**
 * 答复接收管理器
 *
 * @author noear
 * @since 2.0
 */
public interface AcceptorManger {
    /**
     * 添加接收器
     *
     * @param sid      流Id
     * @param acceptor 答复接收器
     */
    void addAcceptor(String sid, AcceptorBase acceptor);

    /**
     * 获取接收器
     *
     * @param sid 流Id
     */
    Acceptor getAcceptor(String sid);

    /**
     * 移除接收器
     *
     * @param sid 流Id
     */
    void removeAcceptor(String sid);
}
