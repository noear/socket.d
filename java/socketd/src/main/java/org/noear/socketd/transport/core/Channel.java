package org.noear.socketd.transport.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 通道
 *
 * @author noear
 * @since 2.0
 */
public interface Channel {
    /**
     * 获取附件
     */
    <T> T getAttachment(String name);

    /**
     * 设置附件
     */
    void setAttachment(String name, Object val);

    /**
     * 移除接收器（答复接收器）
     */
    void removeAcceptor(String sid);

    /**
     * 是否有效
     */
    boolean isValid();

    /**
     * 是否已关闭
     */
    int isClosed();

    /**
     * 关闭（1协议关，2用户关）
     * */
    void close(int code);

    /**
     * 获取配置
     */
    Config getConfig();

    /**
     * 获取请求计数（用于背压控制）
     */
    AtomicInteger getRequests();

    /**
     * 设置握手信息
     *
     * @param handshake 握手信息
     */
    void setHandshake(HandshakeInternal handshake);

    /**
     * 获取握手信息
     */
    HandshakeInternal getHandshake();

    /**
     * 获取远程地址
     */
    InetSocketAddress getRemoteAddress() throws IOException;

    /**
     * 获取本地地址
     */
    InetSocketAddress getLocalAddress() throws IOException;

    /**
     * 发送连接（握手）
     *
     * @param url 连接地址
     */
    void sendConnect(String url) throws IOException;

    /**
     * 发送连接确认（握手）
     *
     * @param connectMessage 连接消息
     */
    void sendConnack(Message connectMessage) throws IOException;

    /**
     * 发送 Ping（心跳）
     */
    void sendPing() throws IOException;

    /**
     * 发送 Pong（心跳）
     */
    void sendPong() throws IOException;

    /**
     * 发送 Close
     */
    void sendClose() throws IOException;

    /**
     * 发送
     *
     * @param frame    帧
     * @param acceptor 答复接收器（没有则为 null）
     */
    void send(Frame frame, Acceptor acceptor) throws IOException;

    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    void retrieve(Frame frame, Consumer<Throwable> onError);

    /**
     * 手动重连（一般是自动）
     */
    void reconnect() throws IOException;

    /**
     * 获取会话
     */
    Session getSession();
}
