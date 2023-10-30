package org.noear.socketd.broker;

import java.util.*;

/**
 * 经理人管理
 *
 * @author noear
 * @since 2.0
 */
public class BrokerManager {
    static Map<String, Broker> brokerAll;
    static Broker brokerOne;

    static {
        brokerAll = new HashMap<>();
        ServiceLoader.load(Broker.class).iterator().forEachRemaining(broker -> {
            brokerAll.put(broker.schema(), broker);
            brokerOne = broker;
        });
    }

    /**
     * 获取默认的经理人
     */
    public static Broker getBroker() {
        return brokerOne;
    }

    /**
     * 根据协议架构获取经理人
     */
    public static Broker getBroker(String schema) {
        if (brokerAll.size() == 0) {
            throw new IllegalStateException("No Broker providers were found.");
        }

        return brokerAll.get(schema);
    }
}
