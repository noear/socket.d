package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.StrUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 经纪人监听器基类（实现玩家封闭管理）
 *
 * @author noear
 * @since 2.1
 */
public abstract class BrokerListenerBase implements Listener {
    private Map<String, Session> sessionAll = new ConcurrentHashMap<>();
    //玩家会话
    private Map<String, Set<Session>> playerSessions = new ConcurrentHashMap<>();
    //轮询计数
    private AtomicInteger playerRoundCounter = new AtomicInteger(0);

    /**
     * 获取所有会话（包括没有名字的）
     */
    public Collection<Session> getSessionAll() {
        return sessionAll.values();
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
    public int getPlayerNum(String name) {
        Collection<Session> tmp = getPlayerAll(name);

        if (tmp == null) {
            return 0;
        } else {
            return tmp.size();
        }
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
        if (StrUtils.isEmpty(name)) {
            return null;
        }

        return getPlayerOneDo(name);
    }

    private Session getPlayerOneDo(String name) {
        Collection<Session> tmp = getPlayerAll(name);
        if (tmp == null || tmp.size() == 0) {
            return null;
        } else {
            //查找可用的会话
            List<Session> sessions = new ArrayList<>();
            for(Session s: tmp){
                if(s.isValid() && !s.isClosing()){
                    sessions.add(s);
                }
            }

            if (sessions.size() == 0) {
                return null;
            }

            if (sessions.size() == 1) {
                return sessions.get(0);
            }

            //论询处理
            int counter = playerRoundCounter.incrementAndGet();
            int idx = counter % sessions.size();
            if (counter > 999_999) {
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