package org.noear.socketd.cluster;

import org.noear.socketd.exception.SocketDException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.stream.SendStream;
import org.noear.socketd.transport.stream.RequestStream;
import org.noear.socketd.transport.stream.SubscribeStream;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.StrUtils;

import java.io.IOException;
import java.util.*;

/**
 * 集群客户端会话
 *
 * @author noear
 * @since 2.1
 */
public class ClusterClientSession implements ClientSession {
    //会话集合
    private final List<ClientSession> sessionList;
    //会话id
    private final String sessionId;

    public ClusterClientSession(List<ClientSession> sessions) {
        this.sessionList = sessions;
        this.sessionId = StrUtils.guid();
    }

    /**
     * 获取所有会话
     */
    public List<ClientSession> getSessionAll() {
        return Collections.unmodifiableList(sessionList);
    }

    /**
     * 获取任意一个会话
     *
     * @param diversionOrNull 分流（或者 null）
     * @since 2.3
     */
    public ClientSession getSessionAny(String diversionOrNull) {
        ClientSession session = null;

        if (StrUtils.isEmpty(diversionOrNull)) {
            session = LoadBalancer.getAnyByPoll(sessionList);
        } else {
            session = LoadBalancer.getAnyByHash(sessionList, diversionOrNull);
        }

        if (session == null) {
            throw new SocketDException("No session is available!");
        } else {
            return session;
        }
    }

    /**
     * 获取任意一个会话（轮询负栽均衡）
     *
     * @deprecated 2.3
     */
    @Deprecated
    public ClientSession getSessionOne() {
        return getSessionAny(null);
    }

    @Override
    public boolean isValid() {
        for (ClientSession session : sessionList) {
            if (session.isValid()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isClosing() {
        for (ClientSession session : sessionList) {
            if (session.isClosing()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String sessionId() {
        return sessionId;
    }


    /**
     * 发送
     *
     * @param event  事件
     * @param entity 实体
     * @return 流
     */
    @Override
    public SendStream send(String event, Entity entity) throws IOException {
        ClientSession sender = getSessionAny(null);

        return sender.send(event, entity);
    }

    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event   事件
     * @param entity  实体
     * @param timeout 超时（单位：毫秒）
     * @return 流
     */
    @Override
    public RequestStream sendAndRequest(String event, Entity entity, long timeout) throws IOException {
        ClientSession sender = getSessionAny(null);

        return sender.sendAndRequest(event, entity, timeout);
    }


    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param entity  实体
     * @param timeout 超时（单位：毫秒）
     * @return 流
     */
    @Override
    public SubscribeStream sendAndSubscribe(String event, Entity entity, long timeout) throws IOException {
        ClientSession sender = getSessionAny(null);

        return sender.sendAndSubscribe(event, entity, timeout);
    }

    @Override
    public void preclose() throws IOException {
        for (ClientSession session : sessionList) {
            //某个关闭出错，不影响别的关闭
            RunUtils.runAndTry(session::preclose);
        }
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        for (ClientSession session : sessionList) {
            //某个关闭出错，不影响别的关闭
            RunUtils.runAndTry(session::close);
        }
    }

    /**
     * 重新连接
     */
    @Override
    public void reconnect() throws IOException {
        for (ClientSession session : sessionList) {
            if (session.isValid() == false) {
                session.reconnect();
            }
        }
    }
}