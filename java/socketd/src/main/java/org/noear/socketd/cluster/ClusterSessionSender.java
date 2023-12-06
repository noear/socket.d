package org.noear.socketd.cluster;

import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.SessionSender;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.IoConsumer;
import org.noear.socketd.utils.RunUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 集群会话发送器
 *
 * @author noear
 * @since 2.1
 */
public class ClusterSessionSender implements SessionSender {
    //会话集合
    private final List<Session> sessionSet;
    //轮询计数
    private final AtomicInteger sessionRoundCounter;

    public ClusterSessionSender(List<Session> sessions) {
        this.sessionSet = sessions;
        this.sessionRoundCounter = new AtomicInteger(0);
    }

    /**
     * 获取所有会话
     */
    public List<Session> getSessionAll() {
        return Collections.unmodifiableList(sessionSet);
    }

    /**
     * 获取一个会话（轮询负栽均衡）
     */
    public SessionSender getSessionOne() {
        if (sessionSet.size() == 0) {
            //没有会话
            throw new SocketdException("No session!");
        } else if (sessionSet.size() == 1) {
            //只有一个就不管了
            return sessionSet.get(0);
        } else {
            //查找可用的会话
            List<Session> sessions = sessionSet.stream()
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
            return sessionSet.get(idx);
        }
    }

    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     */
    public void send(String event, Entity content) throws IOException {
        SessionSender sender = getSessionOne();

        sender.send(event, content);
    }

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     */
    public Entity sendAndRequest(String event, Entity content) throws IOException {
        SessionSender sender = getSessionOne();

        return sender.sendAndRequest(event, content);
    }

    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     */
    public Entity sendAndRequest(String event, Entity content, long timeout) throws IOException {
        SessionSender sender = getSessionOne();

        return sender.sendAndRequest(event, content, timeout);
    }

    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     */
    public void sendAndRequest(String event, Entity content, IoConsumer<Entity> consumer) throws IOException {
        SessionSender sender = getSessionOne();

        sender.sendAndRequest(event, content, consumer);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     */
    public void sendAndSubscribe(String event, Entity content, IoConsumer<Entity> consumer) throws IOException {
        SessionSender sender = getSessionOne();

        sender.sendAndSubscribe(event, content, consumer);
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        for (Session session : sessionSet) {
            //某个关闭出错，不影响别的关闭
            RunUtils.runAndTry(session::close);
        }

        sessionSet.clear();
    }
}
