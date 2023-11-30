package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.Utils;

import java.util.*;

/**
 * 经纪人监听器基类（实现 server 封闭管理）
 *
 * @author noear
 * @since 2.1
 */
public class BrokerListenerBase {
    private Object UPDATE_LOCK = new Object();
    //服务端会话
    private Map<String, Set<Session>> serviceSessions = new HashMap<>();

    /**
     * 获取所有服务
     *
     * @param name 服务名
     */
    public Collection<Session> getServiceAll(String name) {
        return serviceSessions.get(name);
    }

    /**
     * 获取一个服务
     *
     * @param name 服务名
     */
    public Session getServiceOne(String name) {
        if (Utils.isEmpty(name)) {
            return null;
        }

        Set<Session> tmp = serviceSessions.get(name);
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

    /**
     * 添加服务
     *
     * @param name    服务名
     * @param session 服务会话
     */
    public void addService(String name, Session session) {
        synchronized (UPDATE_LOCK) {
            //注册服务端
            Set<Session> sessions = serviceSessions.computeIfAbsent(name, n -> new HashSet<>());
            sessions.add(session);
        }
    }

    /**
     * 移除服务
     *
     * @param name    服务名
     * @param session 服务会话
     */
    public void removeService(String name, Session session) {
        synchronized (UPDATE_LOCK) {
            //注销服务端
            Set<Session> sessions = serviceSessions.get(name);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
    }
}