package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 经纪人监听器基类（实现 server 封闭管理）
 *
 * @author noear
 * @since 2.1
 */
public class BrokerListenerBase {
    //服务端会话
    private Map<String, Set<Session>> serviceSessions = new ConcurrentHashMap<>();
    //轮询计数
    private AtomicInteger serviceRoundCounter = new AtomicInteger(0);

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

        //线程安全处理（避免别处有增减）
        List<Session> sessions = new ArrayList<>(tmp);

        if (sessions.size() == 0) {
            return null;
        } else if (sessions.size() == 1) {
            return sessions.get(0);
        } else {
            //论询处理
            int counter = serviceRoundCounter.incrementAndGet();
            int idx = counter % sessions.size();
            if (counter > 999_999_999) {
                serviceRoundCounter.set(0);
            }
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
        //注册服务端
        Set<Session> sessions = serviceSessions.computeIfAbsent(name, n -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        sessions.add(session);
    }

    /**
     * 移除服务
     *
     * @param name    服务名
     * @param session 服务会话
     */
    public void removeService(String name, Session session) {
        //注销服务端
        Set<Session> sessions = serviceSessions.get(name);
        if (sessions != null) {
            sessions.remove(session);
        }
    }
}