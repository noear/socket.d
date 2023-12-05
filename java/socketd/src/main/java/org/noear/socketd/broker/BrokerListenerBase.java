package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 经纪人监听器基类（实现玩家封闭管理）
 *
 * @author noear
 * @since 2.1
 */
public class BrokerListenerBase {
    //玩家会话
    private Map<String, Set<Session>> playerSessions = new ConcurrentHashMap<>();
    //轮询计数
    private AtomicInteger playerRoundCounter = new AtomicInteger(0);

    public Collection<String> getNameAll(){
        return playerSessions.keySet();
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
     * 获取一个玩家会话
     *
     * @param name 名字
     */
    public Session getPlayerOne(String name) {
        if (Utils.isEmpty(name)) {
            return null;
        }

        Set<Session> tmp = playerSessions.get(name);
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
            int counter = playerRoundCounter.incrementAndGet();
            int idx = counter % sessions.size();
            if (counter > 999_999_999) {
                playerRoundCounter.set(0);
            }
            return sessions.get(idx);
        }
    }

    /**
     * 添加玩家会话
     *
     * @param name    名字
     * @param session 玩家会话
     */
    public void addPlayer(String name, Session session) {
        //注册玩家会话
        Set<Session> sessions = playerSessions.computeIfAbsent(name, n -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        sessions.add(session);
    }

    /**
     * 移除玩家会话
     *
     * @param name    名字
     * @param session 玩家会话
     */
    public void removePlayer(String name, Session session) {
        //注销玩家会话
        Set<Session> sessions = playerSessions.get(name);
        if (sessions != null) {
            sessions.remove(session);
        }
    }
}