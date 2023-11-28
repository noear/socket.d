package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 经纪人监听器基类（实现 server 封闭管理）
 *
 * @author noear
 * @since 2.0
 */
public class BrokerListenerBase {
    //服务端会话
    private Map<String, Set<Session>> serverSessions = new ConcurrentHashMap<>();

    /**
     * 获取所有服务端
     *
     * @param name 名字
     */
    protected Collection<Session> getServerAll(String name) {
        return serverSessions.get(name);
    }

    /**
     * 获取一个服务端
     *
     * @param name 名字
     */
    protected Session getServerOne(String name) {
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

    protected synchronized void addServer(String name, Session session) {
        //注册服务端
        Set<Session> sessions = serverSessions.get(name);
        if (sessions == null) {
            sessions = new HashSet<>();
            serverSessions.put(name, sessions);
        }

        sessions.add(session);
    }

    protected synchronized void removeServer(String name, Session session) {
        //注销服务端
        Set<Session> sessions = serverSessions.get(name);
        if (sessions != null) {
            sessions.remove(session);
        }
    }
}