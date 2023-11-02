package org.noear.socketd.core;

import org.noear.socketd.exception.SocketdConnectionException;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通道
 *
 * @author noear
 * @since 2.0
 */
public interface Channel extends Closeable {
    /**
     * 是否有效
     */
    boolean isValid();

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
     * @param handshaker 握手信息
     */
    void setHandshaker(Handshaker handshaker);

    /**
     * 获取握手信息
     */
    Handshaker getHandshaker();

    /**
     * 获取远程地址
     */
    InetSocketAddress getRemoteAddress() throws IOException;

    /**
     * 获取本地地址
     */
    InetSocketAddress getLocalAddress() throws IOException;


    /**
     * 设置活动时间
     */
    void setLiveTime();

    /**
     * 获取活动时间
     */
    long getLiveTime();


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
    void send(Frame frame, Acceptor acceptor) throws IOException, SocketdConnectionException;

    /**
     * 收回（收回答复帧）
     *
     * @param frame 帧
     */
    void retrieve(Frame frame) throws IOException;

    /**
     * 获取会话
     */
    Session getSession();
}
