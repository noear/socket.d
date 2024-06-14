package org.noear.socketd.broker;

import org.noear.socketd.cluster.LoadBalancer;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.StrUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 经纪人监听器基类（实现玩家封闭管理）
 *
 * @author noear
 * @since 2.1
 */
public abstract class BrokerListenerBase {
    private Map<String, Session> sessionAll = new ConcurrentHashMap<>();
    //玩家会话
    private Map<String, Set<Session>> playerSessions = new ConcurrentHashMap<>();

    /**
     * 获取所有会话（包括没有名字的）
     */
    public Collection<Session> getSessionAll() {
        return sessionAll.values();
    }

    /**
     * 获取特定会话
     */
    public Session getSessionById(String sessionId) {
        return sessionAll.get(sessionId);
    }

    /**
     * 获取任意会话（包括没有名字的）
     */
    public Session getSessionAny(){
        return LoadBalancer.getAnyByPoll(sessionAll.values());
    }

    /**
     * 获取会话数量
     * */
    public int getSessionCount(){
        return sessionAll.size();
    }

    /**
     * 获取所有玩家的名字
     */
    public Collection<String> getNameAll() {
        return playerSessions.keySet();
    }

    /**
     * 获取所有玩家数量
     *
     * @param name 名字
     */
    public int getPlayerCount(String name) {
        Collection<Session> tmp = getPlayerAll(name);

        if (tmp == null) {
            return 0;
        } else {
            return tmp.size();
        }
    }

    /**
     * 获取所有玩家数量
     *
     * @param name 名字
     * @deprecated 2.4
     */
    @Deprecated
    public int getPlayerNum(String name) {
        return getPlayerCount(name);
    }

    /**
     * 获取所有玩家会话
     *
     * @param name 名字
     */
    public Collection<Session> getPlayerAll(String name) {
        return playerSessions.get(name);
    }

    /**
     * 获取任意一个玩家会话
     *
     * @param atName 目标名字
     * @since 2.3
     */
    public Session getPlayerAny(String atName, Session requester, Message message) throws IOException {
        if (StrUtils.isEmpty(atName)) {
            return null;
        }

        if (atName.endsWith("!")) {
            atName = atName.substring(0, atName.length() - 1);
            String x_hash = null;

            if(message != null){
                x_hash = message.meta(EntityMetas.META_X_HASH);
            }

            if (StrUtils.isEmpty(x_hash)) {
                if (requester == null) {
                    return LoadBalancer.getAnyByPoll(getPlayerAll(atName));
                } else {
                    //使用请求者 ip 分流
                    return LoadBalancer.getAnyByHash(getPlayerAll(atName), requester.remoteAddress().getHostName());
                }
            } else {
                //使用指定 hash 分流
                return LoadBalancer.getAnyByHash(getPlayerAll(atName), x_hash);
            }
        } else {
            return LoadBalancer.getAnyByPoll(getPlayerAll(atName));
        }
    }

    /**
     * 获取任意一个玩家会话（不支持哈希）
     *
     * @param atName 目标名字
     * @since 2.3
     */
    public Session getPlayerAny(String atName) {
        if (StrUtils.isEmpty(atName)) {
            return null;
        }

        if (atName.endsWith("!")) {
            atName = atName.substring(0, atName.length() - 1);
        }

        return LoadBalancer.getAnyByPoll(getPlayerAll(atName));
    }

    /**
     * 获取任意一个玩家会话
     *
     * @param atName 目标名字
     * @deprecated  2.3
     */
    @Deprecated
    public Session getPlayerOne(String atName){
        return getPlayerAny(atName);
    }

    /**
     * 添加玩家会话
     *
     * @param name    名字
     * @param session 玩家会话
     */
    public void addPlayer(String name, Session session) {
        //注册玩家会话
        if (StrUtils.isNotEmpty(name)) {
            Set<Session> sessions = playerSessions.computeIfAbsent(name, n -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
            sessions.add(session);
        }

        sessionAll.put(session.sessionId(), session);
    }

    /**
     * 移除玩家会话
     *
     * @param name    名字
     * @param session 玩家会话
     */
    public void removePlayer(String name, Session session) {
        //注销玩家会话
        if (StrUtils.isNotEmpty(name)) {
            Collection<Session> sessions = getPlayerAll(name);
            if (sessions != null) {
                sessions.remove(session);
            }
        }

        sessionAll.remove(session.sessionId());
    }
}