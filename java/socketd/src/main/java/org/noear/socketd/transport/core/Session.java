package org.noear.socketd.transport.core;

import org.noear.socketd.transport.client.ClientSession;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * 会话
 *
 * @author noear
 * @since 2.0
 */
public interface Session extends ClientSession, Closeable {
    /**
     * 获取远程地址
     */
    InetSocketAddress remoteAddress() throws IOException;

    /**
     * 获取本地地址
     */
    InetSocketAddress localAddress() throws IOException;

    /**
     * 获取握手信息
     */
    Handshake handshake();

    /**
     * broker player name
     *
     * @since 2.1
     */
    default String name() {
        return param("@");
    }

    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    String param(String name);

    /**
     * 获取握手参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    String paramOrDefault(String name, String def);

    /**
     * 获取握手路径
     */
    String path();

    /**
     * 设置握手新路径
     */
    void pathNew(String pathNew);

    /**
     * 获取所有属性
     */
    Map<String, Object> attrMap();

    /**
     * 是有属性
     *
     * @param name 名字
     */
    boolean attrHas(String name);

    /**
     * 获取属性
     *
     * @param name 名字
     */
    <T> T attr(String name);

    /**
     * 获取属性或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    <T> T attrOrDefault(String name, T def);

    /**
     * 放置属性
     *
     * @param name  名字
     * @param value 值
     */
    <T> Session attrPut(String name, T value);

    /**
     * 是否有效
     */
    boolean isValid();

    /**
     * 是否关闭中
     * */
    boolean isClosing();

    /**
     * 获取会话Id
     */
    String sessionId();

    /**
     * 最后活动时间
     */
    long liveTime();

    /**
     * 手动重连（一般是自动）
     */
    void reconnect() throws IOException;

    /**
     * 手动发送 Ping（一般是自动）
     */
    void sendPing() throws IOException;

    /**
     * 发送告警
     */
    void sendAlarm(Message from, String alarm) throws IOException;

    /**
     * 答复
     *
     * @param from   来源消息
     * @param entity 实体
     */
    void reply(Message from, Entity entity) throws IOException;

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from   来源消息
     * @param entity 实体
     */
    void replyEnd(Message from, Entity entity) throws IOException;
}