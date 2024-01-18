package org.noear.socketd.cluster;

import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.stream.SendStream;
import org.noear.socketd.transport.stream.RequestStream;
import org.noear.socketd.transport.stream.SubscribeStream;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.StrUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 集群客户端会话
 *
 * @author noear
 * @since 2.1
 */
public class ClusterClientSession implements ClientSession {
    //会话集合
    private final List<ClientSession> sessionSet;
    //轮询计数
    private final AtomicInteger sessionRoundCounter;
    //会话id
    private final String sessionId;

    public ClusterClientSession(List<ClientSession> sessions) {
        this.sessionSet = sessions;
        this.sessionId = StrUtils.guid();
        this.sessionRoundCounter = new AtomicInteger(0);
    }

    /**
     * 获取所有会话
     */
    public List<ClientSession> getSessionAll() {
        return Collections.unmodifiableList(sessionSet);
    }

    /**
     * 获取一个会话（轮询负栽均衡）
     */
    public ClientSession getSessionOne() {
        if (sessionSet.size() == 0) {
            //没有会话
            throw new SocketdException("No session!");
        } else if (sessionSet.size() == 1) {
            //只有一个就不管了
            return sessionSet.get(0);
        } else {
            //查找可用的会话
            List<ClientSession> sessions = sessionSet.stream()
                    .filter(s -> s.isValid())
                    .collect(Collectors.toList());

            if (sessions.size() == 0) {
                //没有可用的会话
                throw new SocketdException("No session is available!");
            }

            if (sessions.size() == 1) {
                return sessions.get(0);
            }

            //论询处理
            int counter = sessionRoundCounter.incrementAndGet();
            int idx = counter % sessions.size();
            if (counter > 999_999_999) {
                sessionRoundCounter.set(0);
            }
            return sessions.get(idx);
        }
    }

    @Override
    public boolean isValid() {
        for (ClientSession session : sessionSet) {
            if (session.isValid()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    @Override
    public void reconnect() throws IOException {
        for (ClientSession session : sessionSet) {
            if (session.isValid() == false) {
                session.reconnect();
            }
        }
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
        ClientSession sender = getSessionOne();

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
        ClientSession sender = getSessionOne();

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
        ClientSession sender = getSessionOne();

        return sender.sendAndSubscribe(event, entity, timeout);
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        for (ClientSession session : sessionSet) {
            //某个关闭出错，不影响别的关闭
            RunUtils.runAndTry(session::close);
        }
    }
}