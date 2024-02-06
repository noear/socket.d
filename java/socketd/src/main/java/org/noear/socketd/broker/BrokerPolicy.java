package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 2.3
 */
public class BrokerPolicy {

    //轮询计数
    private static AtomicInteger playerRoundCounter = new AtomicInteger(0);

    private static int playerRoundCounterGet() {
        int counter = playerRoundCounter.incrementAndGet();
        if (counter > 999_999) {
            playerRoundCounter.set(0);
        }
        return counter;
    }

    /**
     * 获取任意一个
     */
    public static Session getAnyByPoll(Collection<Session> tmp) {
        return getAny(tmp, BrokerPolicy::playerRoundCounterGet);
    }

    /**
     * 根据 ip_hash 获取任意一个
     */
    public static Session getAnyByIpHash(Collection<Session> tmp, Session requester) throws IOException {
        String ip = requester.remoteAddress().getHostName();
        return getAny(tmp, ip::hashCode);
    }


    /**
     * 获取任意一个
     */
    public static Session getAny(Collection<Session> tmp, Supplier<Integer> counterSupplier) {
        if (tmp == null || tmp.size() == 0) {
            return null;
        } else {
            //查找可用的会话
            List<Session> sessions = new ArrayList<>();
            for (Session s : tmp) {
                if (s.isValid() && !s.isClosing()) {
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
            int counter = Math.abs(counterSupplier.get());
            int idx = counter % sessions.size();
            return sessions.get(idx);
        }
    }

    /**
     * 获取第一个
     */
    public static Session getFirst(Collection<Session> tmp) {
        if (tmp == null || tmp.size() == 0) {
            return null;
        } else {
            //查找可用的会话
            for (Session s : tmp) {
                if (s.isValid() && !s.isClosing()) {
                    return s;
                }
            }

            return null;
        }
    }
}
