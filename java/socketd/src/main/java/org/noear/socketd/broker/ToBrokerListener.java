package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.EndEntity;
import org.noear.socketd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 转为经纪人监听器
 *
 * @author noear
 * @since 2.0
 */
public class ToBrokerListener implements Listener {
    private static final Logger log = LoggerFactory.getLogger(ToBrokerListener.class);

    //服务端会话
    private Map<String, Set<Session>> serverSessions = new ConcurrentHashMap<>();

    /**
     * 获取服务端
     *
     * @param name 名字
     */
    protected Session getServer(String name) {
        if (Utils.isEmpty(name)) {
            return null;
        }

        Set<Session> tmp = serverSessions.get(name);
        if (tmp == null) {
            return null;
        }

        List<Session> sessions = new ArrayList<>(tmp);

        if (sessions.size() == 0) {
            return null;
        } else if (sessions.size() == 1) {
            return sessions.get(0);
        } else {
            int idx = new Random().nextInt(sessions.size());
            return sessions.get(idx);
        }
    }

    @Override
    public void onOpen(Session session) throws IOException {
        String name = session.at();

        if (Utils.isNotEmpty(name)) {
            //注册服务端
            Set<Session> sessions = serverSessions.get(name);
            if (sessions == null) {
                sessions = new HashSet<>();
                serverSessions.put(name, sessions);
            }

            sessions.add(session);
        }
    }

    @Override
    public void onClose(Session session) {
        String name = session.at();

        if (Utils.isNotEmpty(name)) {
            //注销服务端
            Set<Session> sessions = serverSessions.get(name);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
    }

    @Override
    public void onMessage(Session client, Message message) throws IOException {
        String at = message.at();

        if (at == null) {
            client.sendAlarm(message, "Broker service require '@' meta");
            return;
        }

        Session server = getServer(at);

        if (server != null) {
            if (message.isRequest()) {
                server.sendAndRequest(message.event(), message, reply -> {
                    client.reply(message, reply);
                });
            } else if (message.isSubscribe()) {
                server.sendAndSubscribe(message.event(), message, reply -> {
                    if (reply instanceof EndEntity) {
                        client.replyEnd(message, reply);
                    } else {
                        client.reply(message, reply);
                    }
                });
            } else {
                server.send(message.event(), message);
            }
        } else {
            client.sendAlarm(message, "Broker don't have '@" + at + "' session");
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        if (log.isWarnEnabled()) {
            log.warn("Broker error", error);
        }
    }
}