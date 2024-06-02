package org.noear.socketd.cluster;

import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.utils.SessionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 负载均衡器
 *
 * @author noear
 * @since 2.3
 */
public class LoadBalancer {
    //轮环计数器
    private static AtomicInteger roundCounter = new AtomicInteger(0);

    private static int roundCounterGet() {
        int counter = roundCounter.incrementAndGet();
        if (counter > 999_999) {
            roundCounter.set(0);
        }
        return counter;
    }

    /**
     * 根据 poll 获取任意一个
     */
    public static <T extends ClientSession> T getAnyByPoll(Collection<T> coll) {
        return getAny(coll, LoadBalancer::roundCounterGet);
    }

    /**
     * 根据 hash 获取任意一个
     */
    public static <T extends ClientSession> T getAnyByHash(Collection<T> coll, String diversion) {
        return getAny(coll, diversion::hashCode);
    }


    /**
     * 获取任意一个
     */
    public static <T extends ClientSession> T getAny(Collection<T> coll, Supplier<Integer> randomSupplier) {
        if (coll == null || coll.size() == 0) {
            return null;
        } else {
            //查找可用的会话
            List<T> sessions = new ArrayList<>();
            for (T s : coll) {
                if (SessionUtils.isActive(s)) {
                    sessions.add(s);
                }
            }

            if (sessions.size() == 0) {
                return null;
            }

            if (sessions.size() == 1) {
                return sessions.get(0);
            }

            //随机获取
            int random = Math.abs(randomSupplier.get());
            int idx = random % sessions.size();
            return sessions.get(idx);
        }
    }

    /**
     * 获取第一个
     */
    public static <T extends ClientSession> T getFirst(Collection<T> coll) {
        if (coll == null || coll.size() == 0) {
            return null;
        } else {
            //查找可用的会话
            for (T s : coll) {
                if (SessionUtils.isActive(s)) {
                    return s;
                }
            }

            return null;
        }
    }
}