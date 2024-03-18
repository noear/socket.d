package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.*;

import java.io.IOException;

/**
 * 客户端（用于构建会话）
 *
 * @author noear
 * @since 2.0
 */
public interface Client {
    /**
     * 连接处理
     */
    Client connectHandler(ClientConnectHandler connectHandler);

    /**
     * 心跳处理
     */
    Client heartbeatHandler(ClientHeartbeatHandler heartbeatHandler);

    /**
     * 配置处理
     */
    Client config(ClientConfigHandler configHandler);

    /**
     * 监听
     */
    Client listen(Listener listener);

    /**
     * 打开会话
     */
    ClientSession open();

    /**
     * 打开会话或出异常（即要求第一次是连接成功的）
     */
    ClientSession openOrThow() throws IOException;
}
